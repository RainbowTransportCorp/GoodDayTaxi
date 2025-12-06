package com.gooddaytaxi.dispatch.infrastructure.outbox.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.EventEnvelope;
import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchEventRepository;
import com.gooddaytaxi.dispatch.infrastructure.outbox.entity.DispatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchRequestedEventPublisher {

    private final DispatchEventRepository outboxRepository;
    private final ObjectMapper mapper;

    private static final String EVENT_TYPE = "DISPATCH_REQUESTED";
    private static final String TOPIC = "dispatch.requested";
    private static final String AGGREGATE_TYPE = "Dispatch";
    private static final int PAYLOAD_VERSION = 1;

    @Transactional
    public void save(DispatchRequestedPayload payload) {

        log.info("[REQUESTED-PREPARE] dispatchId={} driverId={}",
            payload.notificationOriginId(), payload.driverId());

        // 1. Envelope 생성
        var envelope = EventEnvelope.of(EVENT_TYPE, PAYLOAD_VERSION, payload);

        // 2. JSON 직렬화
        String json;
        try {
            json = mapper.writeValueAsString(envelope);
        } catch (Exception e) {
            log.error("[REQUESTED-ERROR] serialize fail: {}", e.getMessage());
            throw new IllegalStateException(e);
        }

        // 3. Outbox 저장용 엔티티 생성
        DispatchEvent event = DispatchEvent.pending(
            EVENT_TYPE,
            TOPIC,
            payload.driverId().toString(),   // partition key
            AGGREGATE_TYPE,
            payload.notificationOriginId(),  // dispatchId
            PAYLOAD_VERSION,
            json
        );

        outboxRepository.save(event);

        log.info("[REQUESTED-SAVED] eventId={} dispatchId={}",
            event.getEventId(), payload.notificationOriginId());
    }
}

