package com.gooddaytaxi.payment.presentation.external.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentCreateRequestDto(@NotNull Long amount,
                                      @NotNull String method,
                                      @NotNull UUID passengerId,
                                      @NotNull UUID driverId,
                                      @NotNull UUID tripId) {
}
