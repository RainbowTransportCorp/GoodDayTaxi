package com.gooddaytaxi.dispatch.infrastructure.outbox.adapter;

import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.application.outbox.OutboxEventModel;
import com.gooddaytaxi.dispatch.infrastructure.outbox.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.infrastructure.outbox.repository.DispatchEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
