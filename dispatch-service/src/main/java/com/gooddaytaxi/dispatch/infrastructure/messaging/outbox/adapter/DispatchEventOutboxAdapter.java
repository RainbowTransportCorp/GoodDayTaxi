package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.adapter;

import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.outbox.OutboxEventModel;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository.DispatchEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DispatchEventOutboxAdapter implements DispatchEventOutboxPort {

    private final DispatchEventJpaRepository repository;

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

    @Override
    public List<OutboxEventModel> findPending(int limit) {
        List<DispatchEvent> events =  repository.findPending(limit);

        events.forEach(e ->
                System.out.println("[DEBUG] EVENT ENTITY ID = " + e.getEventId())
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

    @Override
    @Transactional
    public void markPublished(UUID eventId) {
        repository.updateStatusPublished(eventId);
    }
}
