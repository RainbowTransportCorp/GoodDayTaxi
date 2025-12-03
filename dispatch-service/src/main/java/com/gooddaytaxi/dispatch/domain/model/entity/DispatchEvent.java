package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.EventStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_dispatch_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DispatchEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    // FK는 안 건다
    @Column(name = "dispatch_id", nullable = false)
    private UUID dispatchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType; // DISPATCH_CREATED, DISPATCH_ASSIGNED 등

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private EventStatus eventStatus; // PENDING / SENT / FAILED

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "error_message")
    private String errorMessage;

    public static DispatchEvent pending(UUID dispatchId, EventType type, String payload) {
        return DispatchEvent.builder()
                .dispatchId(dispatchId)
                .eventType(type)
                .eventStatus(EventStatus.PENDING)
                .payload(payload)
                .retryCount(0)
                .build();
    }

    public void markSent() {
        this.eventStatus = EventStatus.SENT;
    }

    public void markFailed(String message) {
        this.eventStatus = EventStatus.FAILED;
        this.errorMessage = message;
        this.retryCount++;
    }
}
