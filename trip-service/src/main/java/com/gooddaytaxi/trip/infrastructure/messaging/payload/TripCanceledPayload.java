package com.gooddaytaxi.trip.infrastructure.messaging.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripCanceledPayload(
        UUID tripId,
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String cancelReason,
        LocalDateTime canceledAt
) {
}
