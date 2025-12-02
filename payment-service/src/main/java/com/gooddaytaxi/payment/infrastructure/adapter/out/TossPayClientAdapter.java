package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.command.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.port.out.TosspayClient;
import com.gooddaytaxi.payment.application.result.ExternalPaymentError;
import com.gooddaytaxi.payment.application.result.PaymentConfirmResult;
import com.gooddaytaxi.payment.infrastructure.client.TosspayFeignClient;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossErrorResponse;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmRequestDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TossPayClientAdapter implements TosspayClient {

    private final TosspayFeignClient feignClient;
    private final ObjectMapper objectMapper;


    @Override
    public PaymentConfirmResult confirmPayment(String idempotencyKey, PaymentTossPayCommand command) {
        TossPayConfirmRequestDto request = new TossPayConfirmRequestDto(
                command.paymentKey(),
                command.orderId(),
                command.amount()
        );
        try {
            TossPayConfirmResponseDto response = feignClient.confirmPayment(idempotencyKey, request);

            return new PaymentConfirmResult(
                    true,
                    parseTosspayTime(response.requestedAt()),
                    parseTosspayTime(response.approvedAt()),
                    response.method(),
                    response.easyPay() != null ? response.easyPay().provider() : null,
                    response.totalAmount(),
                    null
            );
        } catch (FeignException e) {
            ExternalPaymentError error = toExternalPaymentError(e);
            return new PaymentConfirmResult(false, null,null,null,null,0, error);
        }
    }

    //토스페이 시간 파싱
    private LocalDateTime parseTosspayTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return LocalDateTime.parse(time, formatter);
    }

    //FeignException을 ExternalPaymentError로 변환
    private ExternalPaymentError toExternalPaymentError(FeignException e) {
        int status = e.status();
        String message = e.contentUTF8();
        try {
            TossErrorResponse err = objectMapper.readValue(message, TossErrorResponse.class);
            return new ExternalPaymentError(status, err.message(), message);
        } catch (Exception ex) {
            return new ExternalPaymentError(status, "Unknown error", message);
        }
    }


}
