package com.gooddaytaxi.payment.presentation.external.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentCreateRequestDto {
    @NotNull
    private final Long amount;

    @NotNull
    private final String method;

    @NotNull
    private final Long passengerId;

    @NotNull
    private final Long driverId;

    @NotNull
    private final UUID tripId;
}
