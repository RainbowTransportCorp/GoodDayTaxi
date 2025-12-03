package com.gooddaytaxi.payment.presentation.external.dto.response;

import java.util.UUID;

public record PaymentReadResponseDto(UUID paymentId,
                                     Long amount,
                                     String Status,
                                     String method,
                                     UUID passengerId,
                                     UUID driverId,
                                     UUID tripId,
                                     AttemptReadResponseDto attemptResult) {
}
