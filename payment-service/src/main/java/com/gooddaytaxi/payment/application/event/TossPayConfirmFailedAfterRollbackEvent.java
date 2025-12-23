package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.application.command.payment.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentError;

import java.util.UUID;

public record TossPayConfirmFailedAfterRollbackEvent(
        UUID paymentId,
        String paymentKey,
        String idempotencyKey,
        int attemptNo,
        ExternalPaymentError error,
        PaymentTossPayCommand command
) {
}
