package com.gooddaytaxi.payment.application.result;

import java.util.UUID;
import java.time.LocalDateTime;

public record PaymentReadResult(UUID paymentId,
                                Long amount,
                                String status,
                                String method,
                                UUID passengerId,
                                UUID driverId,
                                UUID tripId,
                                AttemptReadResult attemptResult,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
}

