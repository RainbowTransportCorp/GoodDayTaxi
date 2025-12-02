package com.gooddaytaxi.payment.infrastructure.client;

import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmRequestDto;
import com.gooddaytaxi.payment.infrastructure.client.dto.TossPayConfirmResponseDto;
import com.gooddaytaxi.payment.infrastructure.config.TossFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "tosspayClient",url="https://api.tosspayments.com", configuration = TossFeignConfig.class)
public interface TosspayFeignClient {

    @PostMapping("/v1/payments/confirm")
    TossPayConfirmResponseDto confirmPayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody TossPayConfirmRequestDto request
    );
}
