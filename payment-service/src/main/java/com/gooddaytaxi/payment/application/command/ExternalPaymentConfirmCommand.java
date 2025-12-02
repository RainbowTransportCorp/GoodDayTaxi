package com.gooddaytaxi.payment.application.command;

public record ExternalPaymentConfirmCommand(
        String externalPaymentKey,    //toss : paymentKey, kakao : tid
        String orderId,
        Long amount
) {
}
