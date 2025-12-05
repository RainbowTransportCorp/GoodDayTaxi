package com.gooddaytaxi.payment.presentation.external.dto.request.payment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentMethodChangeRequestDto(@NotNull UUID paymentId,
                                            @NotNull String method) {
}
