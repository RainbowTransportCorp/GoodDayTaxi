package com.gooddaytaxi.payment.application.result;

import java.time.LocalDateTime;

public record PaymentConfirmResult(
        boolean success,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt,
        String method,
        String provider,
        Integer totalAmount,
        ExternalPaymentError error
) {
}
