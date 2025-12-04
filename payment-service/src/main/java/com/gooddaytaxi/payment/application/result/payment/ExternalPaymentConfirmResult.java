package com.gooddaytaxi.payment.application.result.payment;

import java.time.LocalDateTime;

public record ExternalPaymentConfirmResult(
        boolean success,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,
        String method,
        String provider,
        Integer totalAmount,
        ExternalPaymentError error
) {
}
