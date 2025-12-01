package com.gooddaytaxi.payment.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.port.out.PaymentCommandPort;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossErrorResponse;
import com.gooddaytaxi.payment.presentation.external.mapper.response.PaymentTossPayCommand;
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
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(Payment payment, PaymentAttempt attempt, feign.FeignException e, PaymentTossPayCommand command) throws JsonProcessingException {

        //실패시 결제 청구서 상태를 '결제 실패'로 변경
        payment.updateStatusToFailed();
        int status = e.status();
        String statusMessage = e.contentUTF8();
        TossErrorResponse err = objectMapper.readValue(statusMessage, TossErrorResponse.class);
        log.warn("TossPay confirm Failed for orderId={}: status={}, message={}",
                command.orderId(), status, statusMessage);
        //실패 이유가 네트워크 오류인 경우 Network error로 저장
        if(status == -1) attempt.registerFailReason("Network error");
            //실패 이유가 tosspay 오류인 경우 해당 메시지로 저장
        else {
            String detailReason = findFailReason(payment, command);
            attempt.registerFailReason(err.message(), detailReason);
        }
        payment.addAttempt(attempt);

        // 필요하면 명시적으로 save
        paymentCommandPort.save(payment);
    }

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
