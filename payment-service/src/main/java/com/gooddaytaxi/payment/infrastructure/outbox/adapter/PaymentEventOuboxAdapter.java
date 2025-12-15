package com.gooddaytaxi.payment.infrastructure.outbox.adapter;

import com.gooddaytaxi.payment.application.outbox.OutboxEventModel;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventOutboxPort;
import com.gooddaytaxi.payment.infrastructure.outbox.entity.PaymentEvent;
import com.gooddaytaxi.payment.infrastructure.outbox.persistence.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventOuboxAdapter implements PaymentEventOutboxPort {
    final PaymentEventRepository repository;


    @Override
    public void save(OutboxEventModel model) {
        PaymentEvent event = new PaymentEvent(
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
        return repository.findPending(PageRequest.of(0,limit)).stream()
                .map(event -> new OutboxEventModel(
                        event.getId(),
                        event.getType(),
                        event.getTopic(),
                        event.getMessageKey(),
                        event.getAggregateType(),
                        event.getAggregateId(),
                        event.getPayloadVersion(),
                        event.getPayload()
                ))
                .toList();
    }

    @Override
    public void markPublished(UUID eventId) {
        repository.updateStatusPublished(eventId);
    }
}
