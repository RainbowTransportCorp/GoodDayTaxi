package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "p_dispatch_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispatchEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    /**
     * 이벤트의 유형을 나타내는 값.
     * <p>
     * 이벤트 타입은 향후 세분화 및 확장 가능성을 고려하여
     * DB에는 String 형태로 저장한다.
     * 코드 레벨에서는 Enum을 사용해 허용된 값만 생성하도록 제한한다.
     */
    @Column(nullable = false)
    private String eventType;            // 예: DISPATCH_CREATED

    @Column(nullable = false)
    private String topic;                // 예: dispatch.request.created

    @Column(nullable = false)
    private String messageKey;           // 예: passengerId

    @Column(nullable = false)
    private String aggregateType;        // 예: Dispatch

    @Column(nullable = false)
    private UUID aggregateId;            // 예: dispatchId

    @Column(nullable = false)
    private int payloadVersion;          // 예: 1

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;              // envelope json

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus eventStatus;

    @Column(nullable = false)
    private int retryCount;

    @Column
    private String errorMessage;

    @Column
    private LocalDateTime publishedAt;

    public static DispatchEvent pending(
            String eventType,
            String topic,
            String messageKey,
            String aggregateType,
            UUID aggregateId,
            int payloadVersion,
            String payloadJson
    ) {
        DispatchEvent event = new DispatchEvent();
        event.eventType = eventType;
        event.topic = topic;
        event.messageKey = messageKey;
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.payloadVersion = payloadVersion;
        event.payload = payloadJson;
        event.eventStatus = EventStatus.PENDING;
        event.retryCount = 0;
        return event;
    }

    public void markSent() {
        this.eventStatus = EventStatus.SENT;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed(String message) {
        this.eventStatus = EventStatus.FAILED;
        this.errorMessage = message;
        this.retryCount++;
    }
}