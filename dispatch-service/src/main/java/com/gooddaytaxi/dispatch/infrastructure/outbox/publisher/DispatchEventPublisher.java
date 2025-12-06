package com.gooddaytaxi.dispatch.infrastructure.outbox.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.EventEnvelope;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchCreatedPayload;
import com.gooddaytaxi.dispatch.infrastructure.outbox.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchEventPublisher {

    private final DispatchEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private static final String EVENT_TYPE = "DISPATCH_CREATED";
    private static final String TOPIC = "dispatch.request.created";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int PAYLOAD_VERSION = 1;

    @Transactional
    public void save(DispatchCreatedPayload createdEvent) {

        log.info("[OUTBOX-PREPARE] dispatchId={} eventType={} topic={}",
                createdEvent.dispatchId(), EVENT_TYPE, TOPIC);

        // 1. Envelope 생성
        EventEnvelope<DispatchCreatedPayload> envelope =
                EventEnvelope.of(EVENT_TYPE, PAYLOAD_VERSION, createdEvent);

        log.debug("[OUTBOX-ENVELOPE] dispatchId={} envelope={}",
                createdEvent.dispatchId(), envelope);

        // 2. JSON 직렬화
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(envelope);
        } catch (JsonProcessingException e) {
            log.error("[OUTBOX-ERROR] dispatchId={} reason=ENVELOPE_SERIALIZE_FAIL message={}",
                    createdEvent.dispatchId(), e.getMessage());
            throw new IllegalArgumentException("Envelope serialization failed", e);
        }

        log.debug("[OUTBOX-PAYLOAD] dispatchId={} payload={}",
                createdEvent.dispatchId(), payloadJson);

        // 3. Outbox(Entity) 생성
        DispatchEvent outbox = DispatchEvent.pending(
                EVENT_TYPE,
                TOPIC,
                createdEvent.passengerId().toString(),
                AGGREGATE_TYPE,
                createdEvent.dispatchId(),
                PAYLOAD_VERSION,
                payloadJson
        );

        log.info("[OUTBOX-CREATE] eventId(PENDING)=NEW dispatchId={} aggregateType={} messageKey={}",
                createdEvent.dispatchId(), AGGREGATE_TYPE, createdEvent.passengerId());

        // 4. 저장
        outboxRepository.save(outbox);

        log.info("[OUTBOX-SAVED] eventId={} eventType={} dispatchId={} topic={}",
                outbox.getEventId(), EVENT_TYPE, createdEvent.dispatchId(), TOPIC);
    }
}
