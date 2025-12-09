package com.gooddaytaxi.dispatch.application.outbox;

import java.util.UUID;

public record OutboxEventModel(
        UUID eventId,
        String eventType,
        String topic,
        String messageKey,
        String aggregateType,
        UUID aggregateId,
        int version,
        String payloadJson
) {
    public OutboxEventModel(
            String eventType,
            String topic,
            String messageKey,
            String aggregateType,
            UUID aggregateId,
            int version,
            String payloadJson
    ) {
        this(null, eventType, topic, messageKey, aggregateType, aggregateId, version, payloadJson);
    }
}
