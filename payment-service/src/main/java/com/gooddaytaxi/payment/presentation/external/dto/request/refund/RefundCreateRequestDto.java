package com.gooddaytaxi.payment.presentation.external.dto.request.refund;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RefundCreateRequestDto(@NotNull String reason,
                                     @NotNull String detailReason,
                                     UUID requestId) {
}
