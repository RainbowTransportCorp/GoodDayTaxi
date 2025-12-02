package com.gooddaytaxi.payment.infrastructure.client.dto;

public record TossPayConfirmRequestDto(String paymentKey,
                                       String orderId,
                                       Long amount) {
}
