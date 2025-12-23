package com.gooddaytaxi.payment.application.event.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripEndedPayload(
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long totalDuration,
        BigDecimal totalDistance,
        Long finalFare
) {
}
