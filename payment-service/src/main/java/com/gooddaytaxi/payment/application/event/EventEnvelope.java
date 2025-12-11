package com.gooddaytaxi.payment.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventEnvelope<T> (UUID eventId,
                                String eventType,
                                LocalDateTime occurredAt,
                                int payloadVersion,
                                T payload
                             ){
    public static <T> EventEnvelope<T> of(String eventType, int version, T payload) {
        return new EventEnvelope<>(
                UUID.randomUUID(),
                eventType,
                LocalDateTime.now(),
                version,
                payload
        );
    }
}
