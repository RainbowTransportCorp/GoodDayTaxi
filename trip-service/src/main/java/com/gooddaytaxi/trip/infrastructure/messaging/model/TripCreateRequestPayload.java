package com.gooddaytaxi.trip.infrastructure.messaging.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripCreateRequestPayload(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime assignedAt
) {
}
