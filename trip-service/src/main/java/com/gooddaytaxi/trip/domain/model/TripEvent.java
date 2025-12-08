package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_trip_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", columnDefinition = "UUID")
    private UUID eventId;

    @Column(name = "trip_id", nullable = false, columnDefinition = "UUID")
    private UUID tripId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private TripEventType eventType; // TRIP_CREATED, STARTED, ENDED, CANCELED ...

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload; // JSON 저장

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false, length = 20)
    private TripEventStatus tripeventStatus; // PENDING, SENT, FAILED

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "error_message", length = 200)
    private String errorMessage;



}
