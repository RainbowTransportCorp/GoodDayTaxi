package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.dispatch.application.event.DispatchEventMetadata;
import com.gooddaytaxi.dispatch.application.event.EventEnvelope;
import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 도메인 이벤트를 Outbox 테이블에 저장하기 위한 공통 Publisher 추상 클래스.
 *
 * 실제 메시지 전송(Kafka 등)은 담당하지 않으며,
 * 이벤트 직렬화, 메타데이터 구성, Outbox 저장까지의 공통 흐름만을 책임진다.
 *
 * Application 계층이 메시징 인프라에 직접 의존하지 않도록 하기 위해 사용된다.
 *
 * @param <T> 이벤트 성격에 맞는 payload 타입
 */
@Slf4j
@RequiredArgsConstructor
public abstract class BaseOutboxPublisher<T> {

    private final ObjectMapper objectMapper;
    private final DispatchEventOutboxPort outboxPort;

    /**
     * 이벤트를 Outbox 형식으로 직렬화하여 저장한다.
     *
     * 이 메서드는 이벤트 전송을 수행하지 않으며,
     * 저장된 이벤트는 별도의 Relay 컴포넌트에 의해 전달된다.
     *
     * @param metadata topic, eventType, aggregate, version 등이 담긴 메타데이터
     * @param aggregateId aggregate 식별자 (dispatchId)
     * @param messageKey 동일한 배차 이벤트의 순서를 보장하기 위한 메시지 키
     * @param payload 이벤트 성격에 맞는 payload 타입
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

        // 3. Outbox 엔티티 생성 (PENDING 상태)
        DispatchEvent event = DispatchEvent.pending(
                metadata.eventType(),
                metadata.topic(),
                messageKey,
                metadata.aggregateType(),
                aggregateId,
                metadata.version(),
                payloadJson
        );

        // 4. Outbox 저장 (전송은 Relay가 담당)
        outboxPort.save(event);

        log.info("[DISPATCH-OUTBOX-SAVED] type={} id={} topic={}",
                metadata.eventType(), aggregateId, metadata.topic());
    }
}


