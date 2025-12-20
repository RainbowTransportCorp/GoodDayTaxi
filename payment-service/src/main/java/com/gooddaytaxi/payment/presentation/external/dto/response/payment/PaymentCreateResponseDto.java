package com.gooddaytaxi.payment.presentation.external.dto.response.payment;

import java.util.UUID;


public record PaymentCreateResponseDto(UUID paymentId,
                                       String message) {
}
