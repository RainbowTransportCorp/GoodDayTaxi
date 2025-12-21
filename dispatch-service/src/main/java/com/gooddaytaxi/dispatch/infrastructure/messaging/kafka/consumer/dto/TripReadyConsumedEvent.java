package com.gooddaytaxi.dispatch.infrastructure.messaging.kafka.consumer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripReadyConsumedEvent(
    UUID eventId,
    UUID tripId,
    Payload payload
) {
    public record Payload(
        UUID dispatchId,
        UUID tripId,
        LocalDateTime startTime
    ) {}
}
