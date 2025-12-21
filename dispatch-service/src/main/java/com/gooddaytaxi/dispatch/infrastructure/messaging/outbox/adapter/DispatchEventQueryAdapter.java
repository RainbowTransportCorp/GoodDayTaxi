package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.adapter;

import com.gooddaytaxi.dispatch.application.port.out.query.DispatchEventQueryPort;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository.DispatchEventJpaRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchEventQueryAdapter implements DispatchEventQueryPort {

    private final DispatchEventJpaRepository repository;

    @Override
    public boolean existsTripCreated(UUID dispatchId) {
        return repository.existsByAggregateIdAndEventType(
            dispatchId,
            "TRIP_READY"
        );
    }
}
