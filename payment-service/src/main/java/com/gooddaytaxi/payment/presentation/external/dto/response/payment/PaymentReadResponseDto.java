package com.gooddaytaxi.payment.presentation.external.dto.response.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentReadResponseDto(UUID paymentId,
                                     Long amount,
                                     String status,
                                     String method,
                                     UUID passengerId,
                                     UUID driverId,
                                     UUID tripId,
                                     AttemptReadResponseDto attemptResult,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
}
