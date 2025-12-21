package com.gooddaytaxi.trip.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.trip.application.port.out.AppendTripEventPort;
import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import com.gooddaytaxi.trip.infrastructure.messaging.model.EventEnvelope;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository.TripEventOutboxJpaRepository;
import com.gooddaytaxi.trip.infrastructure.messaging.payload.TripCanceledPayload;
import com.gooddaytaxi.trip.infrastructure.messaging.payload.TripEndedPayload;
import com.gooddaytaxi.trip.infrastructure.messaging.payload.TripLocationUpdatedPayload;
import com.gooddaytaxi.trip.infrastructure.messaging.payload.TripStartedPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripOutboxAppender implements AppendTripEventPort {

    private static final int PAYLOAD_VERSION = 1;
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
                startTime

        );

        UUID eventId = UUID.randomUUID();

        EventEnvelope<TripStartedPayload> envelope = new EventEnvelope<>(
                eventId,
                PAYLOAD_VERSION,
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

    @Override
    @Transactional
    public UUID appendTripEnded(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String pickupAddress,
            String destinationAddress,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BigDecimal totalDistance,
            Long totalDuration,
            Long finalFare
    ) {
        TripEndedPayload payload = new TripEndedPayload(
                tripId,
                tripId, // notificationOriginId
                notifierId,
                dispatchId,
                driverId,
                passengerId,
                pickupAddress,
                destinationAddress,
                startTime,
                endTime,
                totalDistance,
                totalDuration,
                finalFare

        );

        UUID eventId = UUID.randomUUID();

        EventEnvelope<TripEndedPayload> envelope = new EventEnvelope<>(
                eventId,
                PAYLOAD_VERSION,
                TripEventType.TRIP_ENDED.name(),
                LocalDateTime.now(),
                payload
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(envelope);

            TripEventOutbox outbox = TripEventOutbox.createPendingEvent(
                    tripId,
                    TripEventType.TRIP_ENDED,
                    payloadJson
            );

            outboxRepository.save(outbox);
            return eventId;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize TRIP_ENDED event", e);
        }
    }


    @Override
    @Transactional
    public UUID appendTripCanceled(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String cancelReason,
            LocalDateTime canceledAt
    ) {
        TripCanceledPayload payload = new TripCanceledPayload(
                tripId,
                tripId,          // notificationOriginId = tripId
                notifierId,
                dispatchId,
                driverId,
                passengerId,
                cancelReason,
                canceledAt
                // payloadVersion
        );

        UUID eventId = UUID.randomUUID();

        EventEnvelope<TripCanceledPayload> envelope = new EventEnvelope<>(
                eventId,
                PAYLOAD_VERSION,                              // envelope payloadVersion
                TripEventType.TRIP_CANCELED.name(),
                LocalDateTime.now(),
                payload
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(envelope);

            TripEventOutbox outbox = TripEventOutbox.createPendingEvent(
                    tripId,
                    TripEventType.TRIP_CANCELED,
                    payloadJson
            );

            outboxRepository.save(outbox);
            return eventId;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize TRIP_CANCELED event", e);
        }


    }

    @Override
    @Transactional
    public UUID appendTripLocationUpdated(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            String currentAddress,
            String region,
            String previousRegion,
            Long sequence,
            LocalDateTime locationTime
    ) {
        TripLocationUpdatedPayload payload = new TripLocationUpdatedPayload(
                tripId,
                tripId,   // notificationOriginId
                notifierId,
                dispatchId,
                driverId,
                currentAddress,
                region,
                previousRegion,
                sequence,
                locationTime
        );

        UUID eventId = UUID.randomUUID();

        EventEnvelope<TripLocationUpdatedPayload> envelope = new EventEnvelope<>(
                eventId,
                PAYLOAD_VERSION,
                TripEventType.TRIP_LOCATION_UPDATED.name(),
                LocalDateTime.now(),
                payload
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(envelope);

            TripEventOutbox outbox = TripEventOutbox.createPendingEvent(
                    tripId,
                    TripEventType.TRIP_LOCATION_UPDATED,
                    payloadJson
            );

            outboxRepository.save(outbox);
            return eventId;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize TRIP_LOCATION_UPDATED event", e);
        }
    }


}
