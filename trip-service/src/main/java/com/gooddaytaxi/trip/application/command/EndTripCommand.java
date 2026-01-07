package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.application.validator.UserRole;
import java.math.BigDecimal;
import java.util.UUID;

public record EndTripCommand(
        UUID driverId,
        UserRole role,
        BigDecimal totalDistance, // 총 거리 km
        long totalDuration,//분 단위 총 시간
        String paymentMethod // 결제 수단
) {
}
