package com.gooddaytaxi.trip.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.trip.application.port.out.AppendTripEventPort;
import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import com.gooddaytaxi.trip.infrastructure.messaging.model.EventEnvelope;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository.TripEventOutboxJpaRepository;
import com.gooddaytaxi.trip.infrastructure.messaging.payload.TripStartedPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripOutboxAppender implements AppendTripEventPort {

    private final TripEventOutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public UUID appendTripStarted(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String pickupAddress,
            String destinationAddress,
            LocalDateTime startTime
    ) {
        TripStartedPayload payload = new TripStartedPayload(
                tripId,
                tripId,
                notifierId,
                dispatchId,
                driverId,
                passengerId,
                pickupAddress,
                destinationAddress,
                startTime,
                1
        );

        UUID eventId = UUID.randomUUID();

        EventEnvelope<TripStartedPayload> envelope = new EventEnvelope<>(
                eventId,
                TripEventType.TRIP_STARTED.name(),
                LocalDateTime.now(),
                payload
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(envelope);

            TripEventOutbox outbox = TripEventOutbox.createPendingEvent(
                    tripId,
                    TripEventType.TRIP_STARTED,
                    payloadJson
            );

            outboxRepository.save(outbox);
            return eventId;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize TRIP_STARTED event", e);
        }
    }


}
