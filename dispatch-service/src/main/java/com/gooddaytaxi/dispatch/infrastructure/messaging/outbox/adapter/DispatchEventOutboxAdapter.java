package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.adapter;

import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.outbox.OutboxEventModel;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository.DispatchEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchEventOutboxAdapter implements DispatchEventOutboxPort {

    private final DispatchEventJpaRepository repository;

    /**
     * Outbox 이벤트를 PENDING 상태로 저장한다.
     *
     * @param model 저장할 Outbox 이벤트 모델
     */
    @Override
    public void save(OutboxEventModel model) {
        DispatchEvent event = DispatchEvent.pending(
                model.eventType(),
                model.topic(),
                model.messageKey(),
                model.aggregateType(),
                model.aggregateId(),
                model.version(),
                model.payloadJson()
        );

        repository.save(event);
    }

    /**
     * 아직 전송되지 않은(PENDING) Outbox 이벤트를 조회한다.
     *
     * @param limit 한 번에 조회할 최대 이벤트 개수
     * @return 전송 대기 중인 Outbox 이벤트 목록
     */
    @Override
    public List<OutboxEventModel> findPending(int limit) {
        List<DispatchEvent> events =  repository.findPending(limit);

        events.forEach(e ->
                log.debug("[DEBUG] EVENT ENTITY ID = " + e.getEventId())
        );

        return events.stream()
                .map(e -> new OutboxEventModel(
                        e.getEventId(),
                        e.getEventType(),
                        e.getTopic(),
                        e.getMessageKey(),
                        e.getAggregateType(),
                        e.getAggregateId(),
                        e.getPayloadVersion(),
                        e.getPayload()
                ))
                .toList();
    }

    /**
     * Outbox 이벤트를 전송 완료(PUBLISHED) 상태로 변경한다.
     *
     * @param eventId 상태를 변경할 이벤트 식별자
     */
    @Override
    @Transactional
    public void markPublished(UUID eventId) {
        repository.updateStatusPublished(eventId);
    }
}
