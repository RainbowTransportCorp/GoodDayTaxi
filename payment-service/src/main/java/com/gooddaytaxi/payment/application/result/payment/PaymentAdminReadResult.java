package com.gooddaytaxi.payment.application.result.payment;

import java.util.UUID;
import java.time.LocalDateTime;

public record PaymentAdminReadResult(UUID paymentId,
                                     Long amount,
                                     String status,
                                     String method,
                                     LocalDateTime approvedAt,
                                     UUID passengerId,
                                     UUID driverId,
                                     UUID tripId,
                                     AttemptReadResult attemptResult,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
}

