package com.gooddaytaxi.trip.infrastructure.messaging.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripReadyPayload(
        UUID tripId,
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        LocalDateTime startTime
) {
}
