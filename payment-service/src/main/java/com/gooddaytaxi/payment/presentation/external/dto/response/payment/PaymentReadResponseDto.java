package com.gooddaytaxi.payment.presentation.external.dto.response.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentReadResponseDto(UUID paymentId,
                                     UUID tripId,
                                     Long amount,
                                     String status,
                                     String method,
                                     LocalDateTime approvedAt) {
}
