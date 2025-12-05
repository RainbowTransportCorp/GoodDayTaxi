package com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RefundRequestResponseRequestDto(@NotNull UUID requestId,
                                              @NotNull Boolean approve,
                                              @NotBlank String response) {
}
