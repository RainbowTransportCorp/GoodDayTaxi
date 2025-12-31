package com.gooddaytaxi.trip.infrastructure.messaging.outbox.relay;

import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository.TripEventOutboxJpaRepository;
import com.gooddaytaxi.trip.infrastructure.messaging.producer.TripEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripOutboxEventProcessor {
    private final TripEventOutboxJpaRepository outboxRepository;
    private final TripEventPublisher publisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processOne(UUID eventId) {
        TripEventOutbox event = outboxRepository.findById(eventId)
                .orElse(null);

        if (event == null) {
            log.warn("Outbox event not found. eventId={}", eventId);
            return;
        }

        // 이미 처리된 건 스킵 (중복 실행 대비)
        if (event.getEventStatus() != TripEventStatus.PENDING) {
            return;
        }

        try {
            publisher.publishSync(event);
            event.markSent();
        } catch (Exception e) {
            log.error("Publish failed. eventId={}, type={}", event.getEventId(), event.getEventType(), e);
            event.markFailed("PUBLISH_ERR");
        }

        outboxRepository.save(event);
    }
}
