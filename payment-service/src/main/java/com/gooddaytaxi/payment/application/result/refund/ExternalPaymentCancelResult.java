package com.gooddaytaxi.payment.application.result.refund;

import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentError;

import java.time.LocalDateTime;

public record ExternalPaymentCancelResult(
        boolean success,
        LocalDateTime canceledAt,
        Long cancelAmount,
        String transactionKey,
        ExternalPaymentError error
) {
}
