package com.gooddaytaxi.payment.application.result;

import java.util.UUID;

public record PaymentReadResult(UUID paymentId,
                                Long amount,
                                String status,
                                String method,
                                UUID passengerId,
                                UUID driverId,
                                UUID tripId,
                                AttemptReadResult attemptResult) {
}

