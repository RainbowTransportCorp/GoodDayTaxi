package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.application.validator.UserRole;
import java.math.BigDecimal;
import java.util.UUID;

public record EndTripCommand(
        UUID driverId,
        UserRole role,
        BigDecimal totalDistance, // km
        long totalDuration        // 초 단위
) {
}
