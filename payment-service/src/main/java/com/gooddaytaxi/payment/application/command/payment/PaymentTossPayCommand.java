package com.gooddaytaxi.payment.application.command.payment;

public record PaymentTossPayCommand(String paymentKey,
                                    String orderId,
                                    Long amount) {
}
