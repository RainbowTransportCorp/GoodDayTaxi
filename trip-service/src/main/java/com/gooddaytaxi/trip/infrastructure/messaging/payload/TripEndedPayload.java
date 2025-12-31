package com.gooddaytaxi.trip.infrastructure.messaging.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripEndedPayload(
        UUID tripId,                  // Kafka key
        UUID notificationOriginId,     // = tripId
        UUID notifierId,              // x-user-uuid (driver/승객/시스템)
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal totalDistance,     // km
        Long totalDuration,           // seconds or minutes (너 DB 기준)
        Long finalFare             // won

) {
}
