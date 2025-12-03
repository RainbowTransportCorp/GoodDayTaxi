package com.gooddaytaxi.payment.presentation.external.dto.response;

import java.util.UUID;

public record PaymentCancelResponseDto(UUID id, String status) {
}
