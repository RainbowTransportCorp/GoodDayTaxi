package com.gooddaytaxi.trip.infrastructure.messaging.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventEnvelope<T>(
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt,
        T payload
) {
}
