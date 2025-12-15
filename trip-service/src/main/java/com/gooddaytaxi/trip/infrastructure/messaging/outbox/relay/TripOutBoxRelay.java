package com.gooddaytaxi.trip.infrastructure.messaging.outbox.relay;

import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.infrastructure.messaging.consumer.TripCreateRequestConsumer;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository.TripEventOutboxJpaRepository;
import com.gooddaytaxi.trip.infrastructure.messaging.producer.TripEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripOutBoxRelay {
    private final TripEventOutboxJpaRepository outboxRepository;
    private final TripEventPublisher tripEventPublisher;

    @Transactional
    @Scheduled(fixedDelayString = "5000")
    public void relayPendingEvents() {

        List<TripEventOutbox> pendings =
                outboxRepository.findTop100ByEventStatusOrderByCreatedAtAsc(TripEventStatus.PENDING);

        if (pendings.isEmpty()) return;

        for (TripEventOutbox event : pendings) {
            try {
                tripEventPublisher.publishSync(event); // publishSync or publish
                event.markSent();
                outboxRepository.save(event);

            } catch (Exception e) {
                log.error("Relay failed. eventId={}", event.getEventId(), e);
                event.markFailed("PUBLISH_ERR");
                outboxRepository.save(event);
            }
        }
    }
}
