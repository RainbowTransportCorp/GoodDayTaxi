package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.command.payment.ExternalPaymentConfirmCommand;
import com.gooddaytaxi.payment.application.command.refund.ExternalPaymentCancelCommand;
import com.gooddaytaxi.payment.application.port.out.core.ExternalPaymentPort;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentError;
import com.gooddaytaxi.payment.application.result.refund.ExternalPaymentCancelResult;
import com.gooddaytaxi.payment.infrastructure.client.TosspayFeignClient;
import com.gooddaytaxi.payment.infrastructure.client.dto.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPayClientAdapter implements ExternalPaymentPort {

    private final TosspayFeignClient tosspayClient;
    private final ObjectMapper objectMapper;


    @Override
    public ExternalPaymentConfirmResult confirm(String idempotencyKey, ExternalPaymentConfirmCommand command) {
        TossPayConfirmRequestDto request = new TossPayConfirmRequestDto(
                command.externalPaymentKey(),
                command.orderId(),
                command.amount()
        );
        try {
            TossPayConfirmResponseDto response = tosspayClient.confirmPayment(idempotencyKey, request);

            return new ExternalPaymentConfirmResult(
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
            return new ExternalPaymentConfirmResult(false, null,null,null,null,0, error);
        }
    }

    @Override
    public ExternalPaymentCancelResult cancelTosspayPayment(String paymentKey, String idempotencyKey, ExternalPaymentCancelCommand command) {
        TossPayCancelRequestDto requestDto = new TossPayCancelRequestDto(command.cancelReason());
        try {
            TossPayCancelResponseDto response = tosspayClient.cancelPayment(paymentKey, idempotencyKey, requestDto);
            TossCancelDetail cancel = response.cancels().get(0);
            log.info("TossPay cancel cancelAt: {}, cancelAmount: {}, cancelReason: {}, transactionKey: {}",
                    cancel.canceledAt(), cancel.cancelAmount(), cancel.cancelReason(), cancel.transactionKey());
            //취소가 비즈니서적으로 정상 처리되지 않은 경우
            if(!response.status().equals("CANCELED")) {
                log.warn("TossPay cancel failed for TossPay Business error : {}", response);
                return new ExternalPaymentCancelResult(false, null,  null, null,
                        new ExternalPaymentError(0, "TosspayBusiness failed", "Status: " + response.status()));
            }
            //정상 취소 처리된 경우
            return new ExternalPaymentCancelResult(true, parseTosspayTime(cancel.canceledAt()), cancel.cancelAmount(), cancel.transactionKey(), null);

            //Feign 통신 오류 처리
        } catch (FeignException e) {
            ExternalPaymentError error = toExternalPaymentError(e);
            return new ExternalPaymentCancelResult(false, null, null, null, error);
        }
    }

    //토스페이 시간 파싱
    private LocalDateTime parseTosspayTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return LocalDateTime.parse(time, formatter);
    }

    //FeignException을 ExternalPaymentError로 변환
    private ExternalPaymentError toExternalPaymentError(FeignException e) {
        // 1) 네트워크/타임아웃 계열 (대개 여기에 걸림)
        if (e instanceof feign.RetryableException) {
            log.warn("[TOSS_RETRYABLE] msg={}", e.getMessage(), e);
            String msg = String.valueOf(e.getMessage()).toLowerCase();
            // connection refused (포트 안 열림, 즉시 거절)
            if (msg.contains("refused")) {
                return new ExternalPaymentError(-4,"Connection refused",e.getMessage());
            }
            //timeout 구분
            if (msg.contains("connect timed out")) {
                return new ExternalPaymentError(-2, "Connect timeout", e.getMessage());
            }
            if (msg.contains("read timed out") || msg.contains("read timeout")) {
                return new ExternalPaymentError(-3, "Read timeout", e.getMessage());
            }
            return new ExternalPaymentError(-1, "Network error", e.getMessage());
        }
        int status = e.status();
        String message = e.contentUTF8();
        try {
            TossErrorResponse err = objectMapper.readValue(message, TossErrorResponse.class);
            return new ExternalPaymentError(status, err.message(), message);
        } catch (Exception ex) {
            return new ExternalPaymentError(status, "Unknown error", message);
        }
    }


    public ExternalPaymentConfirmResult getPayment(String paymentKey) {
        try {
            TossPayConfirmResponseDto response = tosspayClient.getPayment(paymentKey);

            return new ExternalPaymentConfirmResult(
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
            return new ExternalPaymentConfirmResult(false, null,null,null,null,0, error);
        }

    }
}
