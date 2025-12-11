package com.gooddaytaxi.payment.infrastructure.outbox.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.event.EventEnvelope;
import com.gooddaytaxi.payment.application.outbox.OutboxEventModel;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventOutboxPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public abstract class PaymentBaseOutboxPublisher {

    private final ObjectMapper objectMapper;
    private final PaymentEventOutboxPort outboxPort;

    protected <T> void publish(
            String eventType,
            String topic,
            String aggregateType,
            UUID aggregateId,
            String messageKey,
            int version,
            T payload
    ) {

        // 1. Envelope 생성
        EventEnvelope<T> envelope = EventEnvelope.of(eventType, version, payload);

        // 2. JSON 직렬화
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            log.error("[PAYMENT-OUTBOX-ERROR] eventType={} aggregateId={} error={}",
                    eventType, aggregateId, e.getMessage());
            throw new IllegalArgumentException("Outbox payload serialization failed", e);
        }

        // 3. 포트에 전달할 application 모델 생성
        OutboxEventModel model = new OutboxEventModel(
                eventType,
                topic,
                messageKey,
                aggregateType,
                aggregateId,
                version,
                payloadJson
        );

        // 4. 포트에 전달 (infra adapter에서 실제 outbox entity 생성)
        outboxPort.save(model);

        log.info("[PAYMENT-OUTBOX-SAVED] eventType={} aggregateId={} topic={}",
                eventType, aggregateId, topic);
    }
}
