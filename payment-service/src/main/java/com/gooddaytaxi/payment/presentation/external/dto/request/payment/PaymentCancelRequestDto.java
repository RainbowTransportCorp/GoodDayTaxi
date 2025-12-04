package com.gooddaytaxi.payment.presentation.external.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentCancelRequestDto(@NotNull UUID paymentId,
                                      @NotBlank String cancelReason) {
}
