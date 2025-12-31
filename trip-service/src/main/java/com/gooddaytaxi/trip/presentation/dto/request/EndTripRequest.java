package com.gooddaytaxi.trip.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EndTripRequest(
        @NotNull
        BigDecimal totalDistance,
        @NotNull
        Long totalDuration
) {
}
