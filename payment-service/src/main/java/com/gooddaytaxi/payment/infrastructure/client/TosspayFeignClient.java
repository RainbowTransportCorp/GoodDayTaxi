package com.gooddaytaxi.payment.infrastructure.client;

import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayCancelRequestDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayCancelResponseDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmRequestDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmResponseDto;
import com.gooddaytaxi.payment.infrastructure.config.TossFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "tosspayClient",url="http://127.0.0.1:59999", configuration = TossFeignConfig.class)  //- test용  connect-timeout은 http://192.0.2.1:59999
@FeignClient(name = "tosspayClient",url="https://api.tosspayments.com", configuration = TossFeignConfig.class)
public interface TosspayFeignClient {

    @PostMapping("/v1/payments/confirm")
    TossPayConfirmResponseDto confirmPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody TossPayConfirmRequestDto request
    );

    @PostMapping("/v1/payments/{paymentKey}/cancel")
    TossPayCancelResponseDto cancelPayment(
            @PathVariable("paymentKey") String paymentKey,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody TossPayCancelRequestDto request
    );

    @GetMapping("/v1/payments/{paymentKey}")
    TossPayConfirmResponseDto getPayment(
            @PathVariable("paymentKey") String paymentKey
    );
}
