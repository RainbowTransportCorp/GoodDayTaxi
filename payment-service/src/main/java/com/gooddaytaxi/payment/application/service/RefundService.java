package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.refund.ExternalPaymentCancelCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.application.event.RefundCompletedEvent;
import com.gooddaytaxi.payment.application.event.payload.RefundSettlementPayload;
import com.gooddaytaxi.payment.application.event.RefundSnapshot;
import com.gooddaytaxi.payment.application.event.TossPayRefundCancelFailedAfterRollbackEvent;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.message.SuccessMessage;
import com.gooddaytaxi.payment.application.port.out.core.ExternalPaymentPort;
import com.gooddaytaxi.payment.application.port.out.core.PaymentCommandPort;
import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.core.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventCommandPort;
import com.gooddaytaxi.payment.application.port.out.redis.RedisPort;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.refund.*;
import com.gooddaytaxi.payment.application.validator.PaymentValidator;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.RefundReason;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import com.gooddaytaxi.payment.domain.vo.RefundSortBy;
import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final PaymentCommandPort paymentCommandPort;
    private final PaymentQueryPort paymentQueryPort;
    private final ExternalPaymentPort externalPaymentPort;
    private final RefundRequestQueryPort requestQueryPort;
    private final PaymentEventCommandPort eventCommandPort;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RedisPort redisPort;
    private final PaymentReader paymentReader;
    private final PaymentValidator validator;

    @Transactional
    public RefundCreateResult confirmTosspayRefund(UUID paymentId, RefundCreateCommand command, UUID userId, String role, String idempotencyKey) {
        try {
            //롤이 최고관리자인지 확인
            validator.checkRoleMasterAdmin(UserRole.of(role));

            //해당 결제가 있는지 확인하고 완료 상태인지 검증
            Payment payment = paymentQueryPort.findByIdWithLock(paymentId)
                    .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
            validator.checkPaymentStatusCompleted(payment.getStatus());

            //결제 수단이 토스페이인지 확인
            validator.checkMethodTossPay(payment.getMethod());
            //환불 요청이 있다면 해당 요청이 승인된 상태인지 확인 + 해당 요청의 결제  실제결제가 일치하는지 확인
            if(command.requestId() != null) loadAndValidateApprovedRequest(command, payment.getId());

            //멱등성 키 확인
            //멱등 선점: 같은 idempotencyKey 요청은 60초 동안 재시도 차단
            String redisKey = "payment:refund:idemp:" + idempotencyKey;
            if (!redisPort.setIfAbsent(redisKey, "IN_PROGRESS", Duration.ofSeconds(60))) {
                throw new PaymentException(PaymentErrorCode.IDEMPOTENCY_REFUND_CONFLICT);
            }


            //해당 결제으 마지막 시도의 pamentKey 가져오기
            PaymentAttempt lastAttempt = paymentQueryPort.findLastAttemptByPaymentId(paymentId).orElseThrow(()->new PaymentException(PaymentErrorCode.PAYMENT_ATTEMPT_NOT_FOUND));
            String paymentKey = lastAttempt.getPaymentKey();
            //환불 사유 매핑
            RefundReason reason = RefundReason.of(command.reason());

            //만약 환불 실패 후 다시 시도라면 기존의 환불 객체 가져오기
            Refund refund;
            if(payment.getRefund() != null) {
                refund  = payment.getRefund().resetRefnud(idempotencyKey);
            }else {
                refund = new Refund(
                        reason,
                        command.incidentAt()+"|"+command.incidentSummary(),
                        command.requestId(),
                        idempotencyKey
                );
            }

            //토스에 환불 요청
            ExternalPaymentCancelResult result = externalPaymentPort.cancelTosspayPayment(paymentKey, idempotencyKey, new ExternalPaymentCancelCommand(reason.getDescription()));

            //실패시 실패 기록 및 예외 던지기
            if(!result.success()) {
                // 실패 기록은 롤백시 이벤트 발생후 별도 트랜잭션으로 먼저 확정
                RefundSnapshot snapshot = new RefundSnapshot(
                        refund.getReason().name(),            // 네 Refund 구조에 맞게
                        refund.getDetailReason(),             // incidentAt|incidentSummary 들어간 필드
                        refund.getRequestId(),                // nullable OK
                        idempotencyKey
                );

                applicationEventPublisher.publishEvent(
                        new TossPayRefundCancelFailedAfterRollbackEvent(
                                payment.getId(),
                                snapshot,
                                result.error()
                        )
                );

                throw new PaymentException(PaymentErrorCode.TOSSPAY_CANCEL_FAILED);
            }

            //만약 환불 금액과 결제 금액이 다르면 에러 로그 남기기
            if(!(payment.getAmount().value()==result.cancelAmount()))
                log.error("TossPay cancel amount mismatch for paymentId={}: expected={}, actual={}",
                        payment.getId(), payment.getAmount().value(), result.cancelAmount());

    //        성공시 결제 엔티티에 저장
            refund.success(result.canceledAt(), result.transactionKey());
            payment.registerRefund(refund, true);

            paymentCommandPort.save(payment);
            redisPort.set(redisKey, "COMPLETED", Duration.ofMinutes(3));   //성공 시 COMPLETED로 마킹 후 3분 동안 재호출 방지

            //환불 완료 이벤트 발행
            //refundId 유무와 상관없이 save()가 커밋을 의미하지는 않아 롤백이 가능하므로 커밋 이후에 이벤트 발행되도록 구현
            applicationEventPublisher.publishEvent(
                    new RefundCompletedEvent(payment.getId(), userId)
            );

            return new RefundCreateResult(paymentId, SuccessMessage.REFUND_CREATE_SUUCCESS);
        } catch (PessimisticLockingFailureException | LockTimeoutException e) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_LOCK_TIMEOUT);
        } catch (JpaSystemException e) {
            // 어떤 환경에서는 LockTimeout이 JpaSystemException으로 감싸져 올라올 수 있음
            if (e.getMostSpecificCause() instanceof jakarta.persistence.LockTimeoutException) {
                throw new PaymentException(PaymentErrorCode.PAYMENT_LOCK_TIMEOUT);
            }
            throw e;
        }
    }

    //기사에게 환불 수행 알림
    public RefundCreateResult requestDriverSupportRefund(UUID paymentId, String reason, UUID userId, String role) {
        Payment payment = loadAndValidatePayment(paymentId);
        //권한 체크 - 관리자만 가능
        validator.checkRoleAdminAndMaster(UserRole.of(role));
        //결제 수단이 실물결제인지 확인
        validator.checkMethodPhysicalPayment(payment.getMethod());
        RefundReason refundReason = RefundReason.of(reason);
        //환불 수행 요청 이벤트 발행
        eventCommandPort.publishRefundSettlementCreated(RefundSettlementPayload.from(payment, refundReason, userId));

        return new RefundCreateResult(paymentId, SuccessMessage.REFUND_SETTLEMENT_CREATE_SUUCCESS);
    }

    //실물결제 환불 확인 완료 - 관리자용
    @Transactional
    public RefundCreateResult registerPhysicalRefund(UUID paymentId, RefundCreateCommand command, UUID userId, String role) {
        //롤이 관리자인지 확인
        validator.checkRoleMasterAdmin(UserRole.of(role));

        //해당 결제가 있는지 확인하고 완료 상태인지 검증
        Payment payment = loadAndValidatePayment(paymentId);
        //결제 수단이 실물결제인지 확인
        validator.checkMethodPhysicalPayment(payment.getMethod());
        //환불 요청이 있다면 해당 요청이 승인된 상태인지 확인 + 해당 요청의 결제  실제결제가 일치하는지 확인
        if(command.requestId() != null) loadAndValidateApprovedRequest(command, payment.getId());


        //환불 사유 매핑
        RefundReason reason = RefundReason.of(command.reason());

        //객체 생성
        Refund refund = new Refund(
                reason,
                command.incidentAt()+"|"+command.incidentSummary(),
                command.requestId()
        );

        //실물 환불 집행 시간 기록
        refund.markExecuted(command.executedAt());
        payment.registerRefund(refund, true);

        paymentCommandPort.save(payment);

        //환불 완료 이벤트 발행
        //여기서 이벤트 발행시 refundId가 존재하지 않으므로 트랜잭션 커밋 이후에 이벤트가 발행되도록 구현
        applicationEventPublisher.publishEvent(
                new RefundCompletedEvent(payment.getId(), userId)
        );

        return new RefundCreateResult(paymentId, SuccessMessage.REFUND_CREATE_SUUCCESS);
    }

    //환불 단건조회
    @Transactional(readOnly = true)
    public RefundReadResult getRefund(UUID paymentId, UUID userId, String role) {
        //권한 체크
        UserRole userRole = UserRole.of(role);
        validator.checkRolePassengerAndDriver(userRole);
        Payment payment = paymentQueryPort.findByIdWithRefund(paymentId).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        //환불 정보 가져오기
        Refund refund = getRefund(payment);
        //승객이나 운잔자이면 본인 결제값인지 확인, 관리자는 통과
        validator.checkPassengerAndDriverPermission(userRole, userId, payment.getPassengerId(), payment.getDriverId());

        return new RefundReadResult(
                refund.getStatus().name(),
                payment.getAmount().value(),
                refund.getReason().getDescription(),
                userRole == UserRole.PASSENGER? refund.getDetailReason(): null,
                refund.getRefundedAt()
        );
    }



    //환불 단건조회 - 관리자용
    @Transactional(readOnly = true)
    public RefundAdminReadResult getAdminRefund(UUID paymentId, String role) {
        //권한 체크
        validator.checkRoleAdminAndMaster(UserRole.of(role));

        Payment payment = paymentQueryPort.findByIdWithRefund(paymentId).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        //환불 정보 가져오기
        Refund refund = getRefund(payment);

        return new RefundAdminReadResult(
                refund.getId(),
                refund.getStatus().name(),
                refund.getReason().getDescription(),
                refund.getDetailReason(),
                refund.getRequestId(),
                refund.getRefundedAt(),
                payment.getId(),
                payment.getAmount().value(),
                new PgRefundResult(refund.getTransactionKey(),refund.getPgFailReason(),refund.getCanceledAt()),
                refund.getCreatedAt(),
                refund.getUpdatedAt()
        );
    }

    //환불 검색
    @Transactional(readOnly = true)
    public Page<RefundReadResult> searchRefund(RefundSearchCommand command, UUID userId, String role) {
        UserRole userRole = UserRole.of(role);
        validator.checkRolePassengerAndDriver(userRole);
        //정렬조건 체크
        RefundSortBy.checkValid(command.sortBy()); //enum 검증용

        //pageable 생성
        Pageable pageable = CommonService.toPageable(command.sortAscending(),command.page(), command.size(), command.sortBy());

        Page<Refund> refunds = paymentQueryPort.searchRefunds(
                command.status(),
                command.reason(),
                command.existRequest(),
                userRole == UserRole.PASSENGER? userId : null,
                userRole == UserRole.DRIVER? userId : null,
                command.tripId(),
                command.method(),
                command.minAmount(),
                command.maxAmount(),
                command.startDay(),
                command.endDay(),
                pageable
        );

        return refunds.map(
                refund -> new RefundReadResult(
                        refund.getStatus().name(),
                        refund.getPayment().getAmount().value(),
                        refund.getReason().getDescription(),
                        userRole == UserRole.PASSENGER? refund.getDetailReason(): null,
                        refund.getRefundedAt()
                )
        );
    }

    //환불 검색 - 관리자용
    @Transactional(readOnly = true)
    public Page<RefundAdminReadResult> searchAdminRefund(RefundSearchCommand command, String role) {
        validator.checkRoleAdminAndMaster(UserRole.of(role));
        //정렬조건 체크
        RefundSortBy.checkValid(command.sortBy()); //enum 검증용
        //pageable 생성
        Pageable pageable = CommonService.toPageable(command.sortAscending(),command.page(), command.size(), command.sortBy());

        Page<Refund> refunds = paymentQueryPort.searchRefunds(
                command.status(),
                command.reason(),
                command.existRequest(),
                command.passengerId(),
                command.driverId(),
                command.tripId(),
                command.method(),
                command.minAmount(),
                command.maxAmount(),
                command.startDay(),
                command.endDay(),
                pageable
        );

        return refunds.map(
                refund -> new RefundAdminReadResult(
                        refund.getId(),
                        refund.getStatus().name(),
                        refund.getReason().getDescription(),
                        refund.getDetailReason(),
                        refund.getRequestId(),
                        refund.getRefundedAt(),
                        refund.getPayment().getId(),
                        refund.getPayment().getAmount().value(),
                        new PgRefundResult(refund.getTransactionKey(),refund.getPgFailReason(),refund.getCanceledAt()),
                        refund.getCreatedAt(),
                        refund.getUpdatedAt()
                )
        );
    }

    //디버그용 - 토스페이 외부결제 정보 조회
    public String getExternalTossPay(UUID paymentId) {
        Payment payment = paymentQueryPort.findById(paymentId).orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String paymentKey = payment.getAttempts().get(0).getPaymentKey();
        log.info("DEBUG local cancel paymentKey={}", paymentKey);

        ExternalPaymentConfirmResult dto = externalPaymentPort.getPayment(paymentKey);
        return dto.toString();
    }

    private Payment loadAndValidatePayment(UUID paymentId) {
        Payment payment = paymentReader.getPayment(paymentId);
        validator.checkPaymentStatusCompleted(payment.getStatus());
        return payment;
    }

    private void loadAndValidateApprovedRequest(RefundCreateCommand command, UUID paymentId) {
        RefundRequest request = requestQueryPort.findById(command.requestId()).orElseThrow(() -> new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_FOUND));
        validator.checkRefundRequestApproved(request.getStatus());
        if(!request.getPaymentId().equals(paymentId))
            throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_PAYMENT_MISMATCH);
    }
    private Refund getRefund(Payment payment) {
        return Optional.ofNullable(payment.getRefund())
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.REFUND_NOT_FOUND));
    }
}
