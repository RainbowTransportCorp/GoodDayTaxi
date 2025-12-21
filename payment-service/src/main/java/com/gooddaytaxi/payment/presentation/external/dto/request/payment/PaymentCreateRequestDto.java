package com.gooddaytaxi.payment.presentation.external.dto.request.payment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentCreateRequestDto(@NotNull Long amount,
                                      @NotNull String method,
                                      @NotNull UUID passengerId,
                                      @NotNull UUID tripId) {
}
