package com.gooddaytaxi.trip.infrastructure.messaging.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripLocationUpdatedPayload(
        UUID tripId,                 // Kafka key
        UUID notificationOriginId,   // = tripId
        UUID notifierId,             // driverId
        UUID dispatchId,
        UUID driverId,

        String currentAddress,
        String region,
        String previousRegion,

        Long sequence,
        LocalDateTime locationTime
) {
}
