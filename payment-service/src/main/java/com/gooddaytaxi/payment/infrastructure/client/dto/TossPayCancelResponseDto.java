package com.gooddaytaxi.payment.infrastructure.client.dto;

import java.util.List;

public record TossPayCancelResponseDto(
        String paymentKey,
        String orderId,
        String status,
        List<TossCancelDetail> cancels
) {
}
