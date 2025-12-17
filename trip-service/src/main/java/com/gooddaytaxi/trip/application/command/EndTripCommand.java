package com.gooddaytaxi.trip.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record EndTripCommand(
        UUID notifierId,
        BigDecimal totalDistance, // km
        long totalDuration        // 초 단위
) {
}
