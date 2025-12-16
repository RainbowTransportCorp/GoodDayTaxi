package com.gooddaytaxi.trip.infrastructure.messaging.outbox.relay;

import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.infrastructure.messaging.consumer.TripCreateRequestConsumer;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository.TripEventOutboxJpaRepository;
import com.gooddaytaxi.trip.infrastructure.messaging.producer.TripEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripOutBoxRelay {
    private final TripEventOutboxJpaRepository outboxRepository;
    private final TripOutboxEventProcessor processor;

    @Value("${trip.outbox.batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${trip.outbox.relay-delay-ms:5000}")
    public void relayPendingEvents() {
        List<TripEventOutbox> pendings =
                outboxRepository.findByEventStatusOrderByCreatedAt(
                        TripEventStatus.PENDING,
                        PageRequest.of(0, batchSize)
                );

        if (pendings.isEmpty()) return;

        for (TripEventOutbox event : pendings) {
            try {
                processor.processOne(event.getEventId()); // ✅ 1건 단위 트랜잭션
            } catch (Exception e) {
                // 여기서는 “로깅만” (실제 상태 변경은 processor 내부에서 처리)
                log.error("Outbox processing failed. eventId={}", event.getEventId(), e);
            }
        }
    }
}
