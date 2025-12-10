package com.gooddaytaxi.payment.infrastructure.outbox.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="p_payment_events")
@Getter
@NoArgsConstructor
public class PaymentEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="event_id")
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String messageKey;           // 예: passengerId

    @Column(nullable = false)
    private String aggregateType;        // 예: Dispatch

    @Column(nullable = false)
    private UUID aggregateId;   // 예: dispatchId

    @Column(nullable = false)
    private int payloadVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentEventStatus status;

    @Column(nullable = false)
    private int retryCount;

    private String errorMessage;

    private LocalDateTime publishedAt;


    public PaymentEvent (String type, String topic, String messageKey, String aggregateType,UUID aggregateId, int payloadVersion ,String payload ) {
        this.type = type;
        this.topic = topic;
        this.messageKey = messageKey;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payloadVersion = payloadVersion;
        this.payload = payload;
        this.status = PaymentEventStatus.PENDING;
        this.retryCount = 0;
        this.publishedAt = LocalDateTime.now();
    }
}
