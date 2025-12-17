package com.gooddaytaxi.trip.infrastructure.messaging.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripStartedPayload(
        UUID tripId,                  // Kafka key
        UUID notificationOriginId,     // = tripId
        UUID notifierId,              // x-user-uuid (driver/승객/시스템)
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        int payloadVersion
) {
}
