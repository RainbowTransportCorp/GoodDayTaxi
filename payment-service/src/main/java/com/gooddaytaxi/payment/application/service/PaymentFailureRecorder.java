package com.gooddaytaxi.payment.application.service;

import com.gooddaytaxi.payment.application.command.payment.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.port.out.core.PaymentCommandPort;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentError;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.entity.Refund;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/*
* tosspay 결제 실패시 결제 시도 기록 및 결제 상태를 실패로 변경하는 서비스
* */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailureRecorder {

    private final PaymentCommandPort paymentCommandPort;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordConfirmFailure(Payment payment, PaymentAttempt attempt, ExternalPaymentError error, PaymentTossPayCommand command) {

        //실패시 결제 청구서 상태를 '결제 실패'로 변경
        payment.updateStatusToFailed();
        log.warn("TossPay confirm Failed for orderId={}: status={}, message={}",
                command.orderId(), error.status(), error.rawBody());

        //실패 이유가 네트워크 오류인 경우 Network error로 저장
        if(error.status() == -1) attempt.registerFailReason("Network error");

            //실패 이유가 tosspay 오류인 경우 내서버에서 이유를 찾아서 저장
        else {
            String detailReason = findFailReason(payment, command);
            attempt.registerFailReason(error.providerMessage(), detailReason);
        }
        payment.addAttempt(attempt);

        paymentCommandPort.save(payment);
    }

//  결제 취소 실패 기록
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordCancelFailure(Payment payment, Refund refund, ExternalPaymentError error) {
        log.warn("TossPay cancel Failed for paymentId={}: status={}, message={}",
                payment.getId(), error.status(), error.rawBody());

        //실패 이유가 네트워크 오류인 경우 Network error로 저장
        if(error.status() == -1) refund.registerFailReason("Network error");

            //실패 이유가 tosspay 비즈니스 오류인 경우 토스페이에서 전달한 사유를 저장
        else {
            refund.registerFailReason(error.providerMessage());
        }

        payment.registerRefund(refund, false);

        paymentCommandPort.save(payment);
    }

    //결제 실패 사유 찾기
    private String findFailReason(Payment payment, PaymentTossPayCommand command) {
        //금액 불일치
        if(!Objects.equals(payment.getAmount().value(), command.amount())) {
            return "금액 불일치";
        }
        if(!Objects.equals(payment.getTripId(), UUID.fromString(command.orderId().substring(6)))) {
            return "운행 정보 불일치";
        }
        //이미 승인된 결제
        if(payment.getStatus().name().equals("COMPLETED")) {
            return "이미 승인된 결제";
        }
        //기타 사유
        return "알 수 없음";
    }
}
