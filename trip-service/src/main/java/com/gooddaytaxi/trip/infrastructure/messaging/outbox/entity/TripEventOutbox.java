package com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
@Table(name = "p_trip_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripEventOutbox extends BaseEntity {
    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TripEventType eventType;

    @Type(value = JsonBinaryType.class)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private TripEventStatus eventStatus;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "error_message", length = 255)
    private String errorMessage;

    @Builder
    private TripEventOutbox(
            UUID eventId,
            UUID tripId,
            TripEventType eventType,
            String payload,
            TripEventStatus eventStatus,
            int retryCount,
            String errorMessage
    ) {
        this.eventId = eventId;
        this.tripId = tripId;
        this.eventType = eventType;
        this.payload = payload;
        this.eventStatus = eventStatus;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
    }

    // ===== Static Factory (실무에서 많이 사용) =====
    public static TripEventOutbox createPendingEvent(
            UUID tripId,
            TripEventType eventType,
            String payloadJson
    ) {
        return TripEventOutbox.builder()
                .eventId(UUID.randomUUID())
                .tripId(tripId)
                .eventType(eventType)
                .payload(payloadJson)
                .eventStatus(TripEventStatus.PENDING)
                .retryCount(0)
                .build();
    }

    // ===== Retry 관련 도메인 메서드 =====
    public void markFailed(String error) {
        this.eventStatus = TripEventStatus.FAILED;
        this.retryCount += 1;
        this.errorMessage = error;
    }

    public void markSent() {
        this.eventStatus = TripEventStatus.SENT;
        this.errorMessage = null;
    }
}
