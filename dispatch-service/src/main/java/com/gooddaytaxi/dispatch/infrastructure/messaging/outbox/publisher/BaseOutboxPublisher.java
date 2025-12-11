package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.EventEnvelope;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.outbox.OutboxEventModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseOutboxPublisher<T> {

    private final ObjectMapper objectMapper;
    private final DispatchEventOutboxPort outboxPort;

    /**
     * 메타데이터 기반 Outbox 저장
     */
    protected void publish(
            DispatchEventMetadata metadata,
            UUID aggregateId,
            String messageKey,
            T payload
    ) {
        // 1. Envelope 생성
        EventEnvelope<T> envelope = EventEnvelope.of(
                metadata.eventType(),
                metadata.version(),
                payload
        );

        // 2. JSON 직렬화
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            log.error("[DISPATCH-OUTBOX-ERROR] eventType={} id={} error={}",
                    metadata.eventType(), aggregateId, e.getMessage());
            throw new IllegalArgumentException("Outbox payload serialization failed", e);
        }

        // 3. Outbox 모델 생성
        OutboxEventModel model = new OutboxEventModel(
                metadata.eventType(),
                metadata.topic(),
                messageKey,
                metadata.aggregateType(),
                aggregateId,
                metadata.version(),
                payloadJson
        );

        // 4. 저장
        outboxPort.save(model);

        log.info("[DISPATCH-OUTBOX-SAVED] type={} id={} topic={}",
                metadata.eventType(), aggregateId, metadata.topic());
    }
}

