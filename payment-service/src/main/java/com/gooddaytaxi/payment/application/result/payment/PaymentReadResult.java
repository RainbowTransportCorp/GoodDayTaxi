package com.gooddaytaxi.payment.application.result.payment;

import java.util.UUID;
import java.time.LocalDateTime;

public record PaymentReadResult(UUID paymentId,
                                Long amount,
                                String status,
                                String method,
                                LocalDateTime approvedAt) {
}

