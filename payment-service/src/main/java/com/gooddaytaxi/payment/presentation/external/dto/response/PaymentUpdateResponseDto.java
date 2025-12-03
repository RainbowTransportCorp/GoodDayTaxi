package com.gooddaytaxi.payment.presentation.external.dto.response;

import java.util.UUID;

public record PaymentUpdateResponseDto(UUID paymentId,
                                       Long amount,
                                       String method) {
}
