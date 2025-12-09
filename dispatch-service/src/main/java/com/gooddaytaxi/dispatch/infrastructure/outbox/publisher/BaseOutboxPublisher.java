package com.gooddaytaxi.dispatch.infrastructure.outbox.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.EventEnvelope;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchEventRepository;
import com.gooddaytaxi.dispatch.infrastructure.outbox.entity.DispatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseOutboxPublisher<T> {

    private final ObjectMapper objectMapper;
    private final DispatchEventRepository outboxRepository;

    protected void publish(
            String eventType,
            String topic,
            String aggregateType,
            UUID aggregateId,
            String messageKey,
            int version,
            T payload
    ) {

        // 1. Envelope
        EventEnvelope<T> envelope = EventEnvelope.of(eventType, version, payload);

        // 2. Serialize
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            log.error("[OUTBOX-ERROR] eventType={} aggregateId={} error={}",
                    eventType, aggregateId, e.getMessage());
            throw new IllegalArgumentException("Outbox payload serialize error", e);
        }

        // 3. Outbox entity
        DispatchEvent outbox = DispatchEvent.pending(
                eventType, topic, messageKey,
                aggregateType, aggregateId, version, payloadJson
        );

        // 4. Save
        outboxRepository.save(outbox);

        log.info("[OUTBOX-SAVED] eventType={} aggregateId={} topic={}",
                eventType, aggregateId, topic);
    }
}
