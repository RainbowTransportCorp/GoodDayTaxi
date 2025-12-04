package com.gooddaytaxi.payment.presentation.external.dto.response.payment;

import java.time.LocalDateTime;

public record AttemptReadResponseDto(String status,
                                     String pgMethod,
                                     String pgProvider,
                                     LocalDateTime approvedAt,
                                     String failDetail) {
}
