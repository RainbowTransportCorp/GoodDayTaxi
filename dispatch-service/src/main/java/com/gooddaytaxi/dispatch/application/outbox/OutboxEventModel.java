package com.gooddaytaxi.dispatch.application.outbox;

import java.util.UUID;

public record OutboxEventModel(
        String eventType,
        String topic,
        String messageKey,
        String aggregateType,
        UUID aggregateId,
        int version,
        String payloadJson
) {}
