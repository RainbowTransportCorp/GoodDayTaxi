package com.gooddaytaxi.payment.presentation.external.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentCreateRequestDto(@NotNull Long amount,
                                      @NotNull String method,
                                      @NotNull Long passengerId,
                                      @NotNull Long driverId,
                                      @NotNull UUID tripId) {
}
