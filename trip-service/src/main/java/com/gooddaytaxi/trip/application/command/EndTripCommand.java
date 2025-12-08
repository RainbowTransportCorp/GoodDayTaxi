package com.gooddaytaxi.trip.application.command;

import java.math.BigDecimal;

public record EndTripCommand(
        BigDecimal totalDistance, // km
        long totalDuration        // 초 단위
) {
}
