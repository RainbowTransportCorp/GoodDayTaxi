package com.gooddaytaxi.trip.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.trip.application.command.TripCreateRequestCommand;
import com.gooddaytaxi.trip.application.port.in.HandleTripCreateRequestUseCase;
import com.gooddaytaxi.trip.application.port.out.CreateTripPort;
import com.gooddaytaxi.trip.application.port.out.ExistsTripByDispatchIdPort;
import com.gooddaytaxi.trip.application.port.out.LoadActiveFarePolicyPort;
import com.gooddaytaxi.trip.domain.model.FarePolicy;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import com.gooddaytaxi.trip.infrastructure.messaging.model.EventEnvelope;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository.TripEventOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripCommandService implements HandleTripCreateRequestUseCase {

    private static final int PAYLOAD_VERSION = 1;
    private final ExistsTripByDispatchIdPort existsTripByDispatchIdPort;
    private final CreateTripPort createTripPort;
    private final LoadActiveFarePolicyPort loadActiveFarePolicyPort;
    private final TripEventOutboxJpaRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void handle(TripCreateRequestCommand command) {

        // 1️⃣ 멱등성 체크
        if (existsTripByDispatchIdPort.existsByDispatchId(command.dispatchId())) {
            log.info("[TripCreateRequest] duplicated event ignored. dispatchId={}",
                    command.dispatchId());
            return;
        }

        // 2️⃣ 활성 요금 정책 조회
        FarePolicy activePolicy = loadActiveFarePolicyPort.loadActivePolicy();

        // 3️⃣ Trip 생성
        Trip trip = Trip.createFromDispatch(activePolicy, command);


        Trip saved = createTripPort.createTrip(trip);

        // 4️⃣ TripCreated Outbox 기록
        UUID eventId = UUID.randomUUID();
        LocalDateTime occurredAt = LocalDateTime.now();

        Map<String, Object> payload = Map.of(
                "tripId", saved.getTripId(),
                "dispatchId", command.dispatchId(),
                "driverId", command.driverId(),
                "passengerId", command.passengerId(),
                "policyId", activePolicy.getPolicyId(),
                "createdAt", occurredAt
        );

        EventEnvelope<Map<String, Object>> envelope =
                new EventEnvelope<>(
                        eventId,
                        PAYLOAD_VERSION,
                        TripEventType.TRIP_CREATED.name(),
                        occurredAt,
                        payload
                );

        TripEventOutbox outbox = TripEventOutbox.builder()
                .eventId(eventId)
                .tripId(saved.getTripId())
                .eventType(TripEventType.TRIP_CREATED)
                .payload(writeJson(envelope))
                .eventStatus(TripEventStatus.PENDING)
                .retryCount(0)
                .errorMessage(null)
                .build();

        outboxRepository.save(outbox);

        log.info("[TripCreateRequest] trip created. tripId={}, policyId={}",
                saved.getTripId(), activePolicy.getPolicyId());
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
