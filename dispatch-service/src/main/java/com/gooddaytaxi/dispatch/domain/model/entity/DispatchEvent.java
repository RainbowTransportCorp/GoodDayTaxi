package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.EventStatus;
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

    /*
    Dto 단에서는 String으로 받고, 어플리케이션, 도메인에서는 이벤트 유형별로 다른 역할을 하기 때문에
    유형 별로 구분한 Enum으로 사용하고,
    다시 column으로 저장할 때는 String으로 저장합니다.
     */
    @Column(name = "event_type", nullable = false)
    private String eventType; // DISPATCH_CREATED, DISPATCH_ASSIGNED 등

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private EventStatus eventStatus; // PENDING / SENT / FAILED

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "error_message")
    private String errorMessage;

    public static DispatchEvent pending(UUID dispatchId, String type, String payload) {
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
