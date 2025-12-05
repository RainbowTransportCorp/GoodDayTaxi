package com.gooddaytaxi.payment.presentation.external.dto.response.refund;

import java.util.UUID;

public record RefundCreateResponseDto(UUID paymentId, String message) {
}
