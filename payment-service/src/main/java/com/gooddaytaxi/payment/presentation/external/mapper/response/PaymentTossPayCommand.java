package com.gooddaytaxi.payment.presentation.external.mapper.response;

public record PaymentTossPayCommand(String orderId,
                                    String paymentKey,
                                    Long amount) {
}
