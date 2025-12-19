package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.refund.ExternalPaymentCancelCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundCreateCommand;
import com.gooddaytaxi.payment.application.command.refund.RefundSearchCommand;
import com.gooddaytaxi.payment.application.event.RefundSettlementPayload;
import com.gooddaytaxi.payment.application.event.RefundCompletedEvent;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.message.SuccessMessage;
import com.gooddaytaxi.payment.application.port.out.core.ExternalPaymentPort;
import com.gooddaytaxi.payment.application.port.out.core.PaymentCommandPort;
import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.core.RefundRequestQueryPort;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventCommandPort;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.refund.ExternalPaymentCancelResult;
import com.gooddaytaxi.payment.application.result.refund.RefundCreateResult;
import com.gooddaytaxi.payment.application.result.refund.RefundReadResult;
import com.gooddaytaxi.payment.application.validator.PaymentValidator;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.enums.RefundReason;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import com.gooddaytaxi.payment.domain.vo.RefundSortBy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PaymentFailureRecorder failureRecorder;
    private final PaymentValidator validator;

    @Transactional
    public RefundCreateResult confirmTosspayRefund(UUID paymentId, RefundCreateCommand command, UUID userId, String role) {
        //롤이 최고관리자인지 확인
        validator.checkRoleMasterAdmin(UserRole.of(role));

        //해당 결제가 있는지 확인하고 완료 상태인지 검증
        Payment payment = loadAndValidatePayment(paymentId);

        //결제 수단이 토스페이인지 확인
        validator.checkMethodTossPay(payment.getMethod());
        //환불 요청이 있다면 해당 요청이 승인된 상태인지 확인 + 해당 요청의 결제  실제결제가 일치하는지 확인
        if(command.requestId() != null) loadAndValidateApprovedRequest(command, payment.getId());


        //해당 결제으 마지막 시도의 pamentKey 가져오기
        String paymentKey = payment.getAttempts().get(0).getPaymentKey();
        //멱등성 키 생성
        UUID idempotencyKey = UUID.randomUUID();
        //환불 사유 매핑
        RefundReason reason = RefundReason.of(command.reason());


        Refund refund = new Refund(
                reason,
                command.incidentAt()+"|"+command.incidentSummary(),
                command.requestId()
        );
        //토스에 환불 요청
        ExternalPaymentCancelResult result = externalPaymentPort.cancelTosspayPayment(paymentKey, idempotencyKey, new ExternalPaymentCancelCommand(reason.getDescription()));

        //실패시 실패 기록 및 예외 던지기
        if(!result.success()) {
            // 실패 기록은 별도 트랜잭션으로 먼저 확정
            failureRecorder.recordCancelFailure(payment, refund, result.error());

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

        //환불 완료 이벤트 발행
        //refundId 유무와 상관없이 save()가 커밋을 의미하지는 않아 롤백이 가능하므로 커밋 이후에 이벤트 발행되도록 구현
        applicationEventPublisher.publishEvent(
                new RefundCompletedEvent(payment.getId(), userId)
        );

        return new RefundCreateResult(paymentId, SuccessMessage.REFUND_CREATE_SUUCCESS);
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

    //디버그용 - 토스페이 외부결제 정보 조회
    public String getExternalTossPay(UUID paymentId) {
        Payment payment = paymentQueryPort.findById(paymentId).orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String paymentKey = payment.getAttempts().get(0).getPaymentKey();
        log.info("DEBUG local cancel paymentKey={}", paymentKey);

        ExternalPaymentConfirmResult dto = externalPaymentPort.getPayment(paymentKey);
        return dto.toString();
    }

    @Transactional(readOnly = true)
    public RefundReadResult getRefund(UUID paymentId, UUID userId, String role) {
        Payment payment = paymentQueryPort.findById(paymentId).orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        //권한 체크
        UserRole userRole = UserRole.of(role);
        //승객이나 운잔자이면 본인 결제값인지 확인, 관리자는 통과
        if(userRole == UserRole.PASSENGER) validator.checkPassengerPermission(userId, payment.getPassengerId());
        else if(userRole == UserRole.DRIVER) validator.checkDriverPermission(userId, payment.getDriverId());

        //환불 정보 가져오기
        Refund refund = payment.getRefund();

        return new RefundReadResult(
                refund.getId(),
                refund.getStatus().name(),
                refund.getReason().getDescription(),
                refund.getDetailReason(),
                refund.getRequestId(),
                refund.getCanceledAt(),
                refund.getTransactionKey(),
                refund.getPgFailReason(),
                refund.getPayment().getId(),
                refund.getPayment().getAmount().value(),
                refund.getCreatedAt(),
                refund.getUpdatedAt()
        );
    }

    //환불 검색
    @Transactional(readOnly = true)
    public Page<RefundReadResult> searchRefund(RefundSearchCommand command, UUID userId, String role) {
        UUID passeangerId = command.passengerId();
        UUID driverId = command.driverId();
        //승객인 경우 본인 승객아이디로 승객아이디 고정
            if (UserRole.of(role) == UserRole.PASSENGER) {
            passeangerId = userId;
            //기사인 경우 본인 기사아이디로 기사 아이디 고정
        } else if (UserRole.of(role) == UserRole.DRIVER) {
            driverId = userId;
        }
        //정렬조건 체크
        RefundSortBy.checkValid(command.sortBy()); //enum 검증용
        //오름차순/내림차순
        Sort.Direction direction = command.sortAscending() ? Sort.Direction.ASC : Sort.Direction.DESC;

        //데이터 조회
        Pageable pageable = PageRequest.of(command.page()-1, command.size(), Sort.by(direction, command.sortBy()));

        Page<Refund> refunds = paymentQueryPort.searchRefunds(
                command.status(),
                command.reason(),
                command.existRequest(),
                passeangerId,
                driverId,
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
                        refund.getId(),
                        refund.getStatus().name(),
                        refund.getReason().getDescription(),
                        refund.getDetailReason(),
                        refund.getRequestId(),
                        refund.getCanceledAt(),
                        refund.getTransactionKey(),
                        refund.getPgFailReason(),
                        refund.getPayment().getId(),
                        refund.getPayment().getAmount().value(),
                        refund.getCreatedAt(),
                        refund.getUpdatedAt()
                )
        );
    }

    private Payment loadAndValidatePayment(UUID paymentId) {
        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        validator.checkPaymentStatusCompleted(payment.getStatus());
        return payment;
    }

    private void loadAndValidateApprovedRequest(RefundCreateCommand command, UUID paymentId) {
        RefundRequest request = requestQueryPort.findById(command.requestId()).orElseThrow(() -> new PaymentException(PaymentErrorCode.REFUND_REQUEST_NOT_FOUND));
        validator.checkRefundRequestApproved(request.getStatus());
        if(!request.getPaymentId().equals(paymentId))
            throw new PaymentException(PaymentErrorCode.REFUND_REQUEST_PAYMENT_MISMATCH);
    }
}
