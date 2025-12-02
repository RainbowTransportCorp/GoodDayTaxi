package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.EventStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private UUID event_id;

    @Column(name = "dispatch_id", nullable = false)
    private UUID dispatchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private EventStatus eventStatus;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    public static DispatchEvent create(UUID dispatchId, EventType type, String payload) {
        DispatchEvent e = new DispatchEvent();
        e.dispatchId = dispatchId;
        e.eventType = type;
        e.eventStatus = EventStatus.PENDING;
        e.payload = payload;
        return e;
    }
}