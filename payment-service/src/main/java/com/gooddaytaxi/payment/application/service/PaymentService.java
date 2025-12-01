package com.gooddaytaxi.payment.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import com.gooddaytaxi.payment.application.command.PaymentCreateCommand;
import com.gooddaytaxi.payment.application.port.out.PaymentCommandPort;
import com.gooddaytaxi.payment.application.port.out.PaymentQueryPort;
import com.gooddaytaxi.payment.application.result.PaymentCreateResult;
import com.gooddaytaxi.payment.application.result.PaymentTossPayResult;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.vo.Fare;
import com.gooddaytaxi.payment.domain.vo.PaymentMethod;
import com.gooddaytaxi.payment.domain.vo.UserRole;
import com.gooddaytaxi.payment.infrastructure.client.TosspayClient;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.request.PaymentTossPayRequestDto;
import com.gooddaytaxi.payment.presentation.external.mapper.response.PaymentTossPayCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentCommandPort paymentCommandPort;
    private final PaymentQueryPort paymentQueryPort;
    private final TosspayClient tosspayClient;
    private final PaymentFailureRecorder failureRecorder;


    @Transactional
    public PaymentCreateResult createPayment(PaymentCreateCommand command) {
        //승객아이디, 운전자아이디, 탑승아이디 검증은 운행에서 받아올 계획이므로 없음
        // 금액 검증
        Fare amount = Fare.of(command.amount());
        //결제 수단 검증
        PaymentMethod method = PaymentMethod.of(command.method());

        //결제 청구서 생성
        Payment payment = new Payment(amount,  method, command.passengerId(), command.driverId(), command.tripId());

        paymentCommandPort.save(payment);

        return new PaymentCreateResult(payment.getId(), payment.getMethod().name(), payment.getAmount().value());
    }

    //토스페이 결제 준비
    @Transactional
    public Long tosspayReady(UUID userId, String role, UUID tripId) {
        log.info("TossPay Ready called: userId={}, role={}, tripId={}", userId, role, tripId);
        //유저의 역할이 승객인지 확인
        if(UserRole.of(role) != UserRole.PASSENGER) {
            throw new BusinessException(ErrorCode.AUTH_FORBIDDEN_ROLE);
        }
        //운행 아이디로 결제 청구서 조회
        Payment payment = paymentQueryPort.findByTripId(tripId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));

        log.debug("TossPay Payment found for tripId={}", tripId);
        //해당 승객이 맞는지 확인
        if(!Objects.equals(payment.getPassengerId(), userId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        //해당 결제 청구서의 상태를 '결제 진행 중'으로 변경
        payment.updateStatusToProcessing();
        log.debug("Tosspay Payment status updated to IN_PROCESS for tripId={}", tripId);

        //해당 결제 청구서의 금액 반환
        return payment.getAmount().value();
    }

    //토스페이 결제 승인
    @Transactional
    public PaymentTossPayResult confirmTossPayment(PaymentTossPayCommand command) throws JsonProcessingException {
        log.info("TossPay Confirm Payment called: paymentKey={}, orderId={}, amount={}",
                command.paymentKey(), command.orderId(), command.amount());

        //해당 결제 청구서 조회
        Payment payment = paymentQueryPort.findByTripId(UUID.fromString(command.orderId().substring(6)))
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));

        //멱등성 키 만들고 새 결제 시도 기록
        UUID idempotencyKey = UUID.randomUUID();

        //시도 횟수 계산
        int attemptNo = payment.getAttempts().size()+1;
        PaymentAttempt attempt = new PaymentAttempt(command.paymentKey(), idempotencyKey, attemptNo);

        try {
            //tosspay 결제 승인 요청
            TossPayConfirmResponseDto result = tosspayClient.confirmPayment(idempotencyKey.toString(), new PaymentTossPayRequestDto(command.paymentKey(), command.orderId(), command.amount()));

            LocalDateTime requestedAt = parseTosspayTime(result.requestedAt());
            LocalDateTime approvedAt = parseTosspayTime(result.approvedAt());
            //성공시 결제 청구서 상태를 '결제 완료'로 변경
            attempt.registerConfirmTosspay(requestedAt, approvedAt, result.method());
            if(result.method().equals("간편결제")) {
                attempt.registerProvider(result.easyPay().provider());
            }
            payment.addAttempt(attempt);
            payment.updateStatusToComplete();  //처리중에서 완료로 변경
            log.info("TossPay Payment confirmed successfully for orderId={}, requestedAt={}, approveAt={}", command.orderId(), requestedAt, approvedAt);

            return new PaymentTossPayResult(
                    payment.getId(),
                    payment.getAmount().value(),
                    payment.getStatus().name(),
                    payment.getMethod().name()
            );

        }catch (feign.FeignException e) {

            // 🔥 실패 기록은 별도 트랜잭션으로 먼저 확정
            failureRecorder.recordFailure(payment, attempt, e, command);

            //최종적으로 비즈니스 예외 던지기
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR);
        }

    }

    private LocalDateTime parseTosspayTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return LocalDateTime.parse(time, formatter);
    }



}
