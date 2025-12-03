package com.gooddaytaxi.payment.application.command;

public record PaymentTossPayCommand(String paymentKey,
                                    String orderId,
                                    Long amount) {
}
