package com.gooddaytaxi.payment.presentation.external.dto.response.refund;

import java.time.LocalDateTime;

public record RefundReadResponseDto(
        String status,
        Long amount,
        String reason,
        String detailReason,
        LocalDateTime refundedAt
) {
}
