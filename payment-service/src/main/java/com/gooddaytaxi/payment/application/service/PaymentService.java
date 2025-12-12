package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.payment.*;
import com.gooddaytaxi.payment.application.event.PaymentCompletePayload;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.port.out.core.ExternalPaymentPort;
import com.gooddaytaxi.payment.application.port.out.core.PaymentCommandPort;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventCommandPort;
import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.application.result.payment.*;
import com.gooddaytaxi.payment.application.validator.PaymentValidator;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.enums.PaymentMethod;
import com.gooddaytaxi.payment.domain.enums.PaymentStatus;
import com.gooddaytaxi.payment.domain.enums.UserRole;
import com.gooddaytaxi.payment.domain.vo.Fare;
import com.gooddaytaxi.payment.domain.vo.PaymentSortBy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class PaymentService {

    private final PaymentCommandPort paymentCommandPort;
    private final PaymentQueryPort paymentQueryPort;
    private final ExternalPaymentPort externalPaymentPort;
    private final PaymentEventCommandPort eventCommandPort;
    private final PaymentFailureRecorder failureRecorder;
    private final PaymentValidator validator;


    @Transactional
    public PaymentCreateResult createPayment(PaymentCreateCommand command) {
        //승객아이디, 운전자아이디, 탑승아이디 검증은 운행에서 받아올 계획이므로 없음
        UUID tripId = command.tripId();

        //해당 여행 아이디로 이미 결제 청구서가 존재하는지 확인
        // 대기, 진행중, 실패, 완료된 청구서는 다시 생성 불가
        //취소되었거나 환불된 청구서는 재생성 가능
        if(paymentQueryPort.existByTripIdAndNotStatusForCreate(tripId)) {
            Payment payment = paymentQueryPort.findLastByTripIdAndStatusForCreate(tripId);
            PaymentStatus status = payment.getStatus();
            if(status.equals(PaymentStatus.PENDING)
                    || status.equals(PaymentStatus.IN_PROCESS)
                    || status.equals(PaymentStatus.FAILED))
                throw new PaymentException(PaymentErrorCode.DUPLICATE_PAYMENT_EXISTS);
            else if (status.equals(PaymentStatus.COMPLETED)) throw new PaymentException(PaymentErrorCode.COMPLETED_PAYMENT);
        }
//        paymentQueryPort.
        // 금액 검증
        Fare amount = Fare.of(command.amount());
        //결제 수단 검증
        PaymentMethod method = PaymentMethod.of(command.method());

        //결제 청구서 생성
        Payment payment = new Payment(amount,  method, command.passengerId(), command.driverId(), tripId);

        paymentCommandPort.save(payment);

        return new PaymentCreateResult(payment.getId(), payment.getMethod().name(), payment.getAmount().value());
    }

    //토스페이 결제 준비
    @Transactional
    public Long tosspayReady(UUID userId, String role, UUID tripId) {
        log.info("TossPay Ready called: userId={}, role={}, tripId={}", userId, role, tripId);
        //유저의 역할이 승객인지 확인
        validator.checkRolePassenger(UserRole.of(role));
        //운행 아이디로 결제 청구서 조회
        Payment payment = paymentQueryPort.findLastByTripIdAndStatusForCreate(tripId);

        log.debug("TossPay Payment found for tripId={}", tripId);
        //해당 승객이 맞는지 확인
        validator.checkPassengerPermission(userId, payment.getPassengerId());

        //결제 수단이 토스페이인지 확인
        validator.checkMethodTossPay(payment.getMethod());

        //결제 전 상태인지 확인
        validator.checkStatusBeforePayment(payment.getStatus());

        //해당 결제 청구서의 상태를 '결제 진행 중'으로 변경
        payment.updateStatusToProcessing();
        log.debug("Tosspay Payment status updated to IN_PROCESS for tripId={}", tripId);

        //해당 결제 청구서의 금액 반환
        return payment.getAmount().value();
    }

    //토스페이 결제 승인
    @Transactional
    public PaymentApproveResult approveTossPayment(PaymentTossPayCommand command, UUID userId, String role) {
        log.info("TossPay External Confirm Payment requested: paymentKey={}, orderId={}, amount={}",
                command.paymentKey(), command.orderId(), command.amount());

        //유저의 역할이 승객인지 확인
        validator.checkRolePassenger(UserRole.of(role));

        //해당 결제 청구서 조회
        Payment payment = paymentQueryPort.findLastByTripIdAndStatusForCreate(UUID.fromString(command.orderId().substring(6)));

        //결제 수단이 토스페이인지 확인
        validator.checkMethodTossPay(payment.getMethod());

        //결제 청구서 상태가 '결제 진행 중'인지 확인
        if(!(payment.getStatus() == PaymentStatus.IN_PROCESS)) throw new PaymentException(PaymentErrorCode.PAYMENT_STATUS_INVALID);


        //멱등성 키 생성
        UUID idempotencyKey = UUID.randomUUID();

        //시도 횟수 계산
        int attemptNo = payment.getAttempts().size()+1;
        PaymentAttempt attempt = new PaymentAttempt(command.paymentKey(), idempotencyKey, attemptNo);


        //tosspay 결제 승인 요청
        ExternalPaymentConfirmResult result = externalPaymentPort.confirm(idempotencyKey.toString(),
                new ExternalPaymentConfirmCommand(command.paymentKey(), command.orderId(), command.amount()));

        //실패시 실패 기록 및 예외 던지기
        if (!result.success()) {
            // 실패 기록은 별도 트랜잭션으로 먼저 확정
            failureRecorder.recordConfirmFailure(payment, attempt,result.error() , command);

            //최종적으로 비즈니스 예외 던지기
            throw new PaymentException(PaymentErrorCode.TOSSPAY_CONFIRM_FAILED);
        }

        //성공시 결제 청구서 상태를 '결제 완료'로 변경
        attempt.registerApproveTosspay(result.requestedAt(), result.approvedAt(), result.method(), result.provider());

        //데이터 저장
        payment.addAttempt(attempt);
        payment.updateStatusToComplete();  //처리중에서 완료로 변경

        log.info("TossPay Payment approved successfully for orderId={}, requestedAt={}, approveAt={}", command.orderId(), result.requestedAt(), result.approvedAt());

        //이벤트 발행
        eventCommandPort.publishPaymentCompleted(PaymentCompletePayload.from(payment,userId));

        return new PaymentApproveResult(
                payment.getId(),
                payment.getAmount().value(),
                payment.getStatus().name(),
                payment.getMethod().name()
        );
    }

    //기사가 탑승자에게 현금, 카드로 직접 결제 후 완료 처리
    @Transactional
    public PaymentApproveResult approveDriverPayment(UUID paymentId, UUID userId, String role) {
        log.info("Driver Pay Payment called: paymentId={}", paymentId);

        //유저의 역할이 기사인지 확인
        validator.checkRoleDriver(UserRole.of(role));

        //운행 아이디로 결제 청구서 조회
        Payment payment = paymentQueryPort.findById(paymentId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //해당 기사가 맞는지 확인
        validator.checkDriverPermission(userId, payment.getDriverId());

        //결제 청구서 상태가 대기인지 확인
        validator.checkStatusPending(payment.getStatus());

        //결제 수단이 외부 결제가 아닌 경우만 가능
        validator.checkMethodNotExternalPayment(payment.getMethod());

        //해당 결제 청구서의 상태를 결제 완료로 변경
        payment.updateStatusToComplete();

        //이벤트 발행
        eventCommandPort.publishPaymentCompleted(PaymentCompletePayload.from(payment, userId));

        return new PaymentApproveResult(
                payment.getId(),
                payment.getAmount().value(),
                payment.getStatus().name(),
                payment.getMethod().name()
        );
    }

    //결제 청구서 단건 조회
    public PaymentReadResult getPayment(UUID paymentId) {
        Payment payment = paymentQueryPort.findById(paymentId).orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        AttemptReadResult attemptResult = null;
        //결제 수단이 토스페이인경우 마지막 결제 내용도 포함
        if(payment.getMethod() == PaymentMethod.TOSS_PAY) {
            PaymentAttempt lastAttempt = payment.getAttempts().get(0);
            attemptResult = new AttemptReadResult(
                    lastAttempt.getStatus().toString(),
                    lastAttempt.getPgMethod(),
                    lastAttempt.getPgProvider(),
                    lastAttempt.getPgApprovedAt(),
                    lastAttempt.getFailDetail()
            );
        }
        return new PaymentReadResult(
                payment.getId(),
                payment.getAmount().value(),
                payment.getStatus().name(),
                payment.getMethod().name(),
                payment.getPassengerId(),
                payment.getDriverId(),
                payment.getTripId(),
                attemptResult,
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    //결제 청구서 검색
    @Transactional(readOnly = true)
    public Page<PaymentReadResult> searchPayment(PaymentSearchCommand command, UUID userId, String role) {
        UUID passeangerId = command.passengerId();
        UUID driverId = command.driverId();
        //승객인 경우 본인 승객아이디로 승객아이디 고정
        if (UserRole.of(role) == UserRole.PASSENGER) {
            passeangerId = userId;
        //기사인 경우 본인 기사아이디로 기사 아이디 고정
        } else if (UserRole.of(role) == UserRole.DRIVER) {
            driverId = userId;
        }
        //매니저이면 모두 가능

        //정렬조건 체크
        PaymentSortBy.checkValid(command.sortBy()); //enum 검증용
        //오름차순/내림차순
        Sort.Direction direction = command.sortAscending() ? Sort.Direction.ASC : Sort.Direction.DESC;

        //데이터 조회
        Pageable pageable = PageRequest.of(command.page()-1, command.size(), Sort.by(direction, command.sortBy()));
        Page<Payment> payments = paymentQueryPort.searchPayments(
                command.method(),
                command.status(),
                passeangerId,
                driverId,
                command.tripId(),
                command.startDay(),
                command.endDay(),
                pageable
        );

        //결과값 반환
        return payments.map(payment -> {
            AttemptReadResult attemptResult = null;
            if(payment.getMethod() == PaymentMethod.TOSS_PAY) {
                //아직 결제 전이면 시도 없음
                if(!(payment.getStatus() == PaymentStatus.PENDING ||payment.getStatus() == PaymentStatus.IN_PROCESS) ){
                    PaymentAttempt lastAttempt = payment.getAttempts().get(0);
                    attemptResult = new AttemptReadResult(
                            lastAttempt.getStatus().toString(),
                            lastAttempt.getPgMethod(),
                            lastAttempt.getPgProvider(),
                            lastAttempt.getPgApprovedAt(),
                            lastAttempt.getFailDetail()
                    );
                }

            }
            return new PaymentReadResult(
                    payment.getId(),
                    payment.getAmount().value(),
                    payment.getStatus().name(),
                    payment.getMethod().name(),
                    payment.getPassengerId(),
                    payment.getDriverId(),
                    payment.getTripId(),
                    attemptResult,
                    payment.getCreatedAt(),
                    payment.getUpdatedAt()
            );
        });

    }

    //결제 금액 변경
    @Transactional
    public PaymentUpdateResult changePaymentAmount(PaymentAmountChangeCommand command, UUID userId, String role) {
        //승객은 무조건 불가
        validator.notAllowedPassenger(UserRole.of(role));


        Payment payment = paymentQueryPort.findById(command.paymentId())
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //결제 금액이 같으면 불가
        if(payment.getAmount().value() == command.amount()) throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_SAME);

        //기사는 해당 청구서의 기사만 가능
        if(UserRole.of(role) == UserRole.DRIVER) validator.checkDriverPermission(userId, payment.getDriverId());

        //결제가 결제 승인 전에만 가능
        validator.checkStatusBeforePayment(payment.getStatus());

        //금액 변경 처리
        payment.changeAmount(Fare.of(command.amount()));

        return new PaymentUpdateResult(
                payment.getId(),
                payment.getAmount().value(),
                payment.getMethod().name()
        );
    }


    //결제 수단 변경
    @Transactional
    public PaymentUpdateResult changePaymentMethod(PaymentMethodChangeCommand command, UUID userId, String role) {
        //승객은 무조건 불가
        if(UserRole.of(role) == UserRole.PASSENGER) throw new PaymentException(PaymentErrorCode.PASSENGER_ROLE_NOT_ALLOWED);


        Payment payment = paymentQueryPort.findById(command.paymentId())
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        PaymentMethod method = PaymentMethod.of(command.method());
        //결제 수단이 같으면 불가
        if(payment.getMethod().equals(method)) throw new PaymentException(PaymentErrorCode.PAYMENT_METHOD_SAME);

        //기사는 해당 청구서의 기사만 가능
        if(UserRole.of(role) == UserRole.DRIVER) validator.checkDriverPermission(userId, payment.getDriverId());

        //결제가 결제 승인 전에만 가능
        validator.checkStatusBeforePayment(payment.getStatus());

        //금액 변경 처리
        payment.changeMethod(method);

        return new PaymentUpdateResult(
                payment.getId(),
                payment.getAmount().value(),
                payment.getMethod().name()
        );
    }

    //결제 취소
    @Transactional
    public PaymentCancelResult cancelPayment(PaymentCancelCommand command, UUID userId, String role) {
        //승객은 무조건 붊가
        validator.notAllowedPassenger(UserRole.of(role));

        Payment payment = paymentQueryPort.findById(command.paymentId())
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //기사는 해당 청구서의 기사만 가능
        if(UserRole.of(role) == UserRole.DRIVER) validator.checkDriverPermission(userId, payment.getDriverId());


        //결제가 완료되었거나(환불 포함) 이미 취소된 결제는 불가
        validator.checkStatusAfterPaymentOrCanceled(payment.getStatus());

        //결제 취소 처리
        payment.cancelPayment(command.cancelReason());

        return new PaymentCancelResult(
                payment.getId(),
                payment.getStatus().name()
        );
    }
    
}
