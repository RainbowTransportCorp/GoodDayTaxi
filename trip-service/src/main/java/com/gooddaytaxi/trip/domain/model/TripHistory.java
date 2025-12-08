package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.trip.domain.model.enums.TripEventType;
import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.domain.model.enums.TripHistoryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_trip_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", columnDefinition = "UUID")
    private UUID historyId;

    @Column(name = "trip_id", nullable = false, columnDefinition = "UUID")
    private UUID tripId;

    @Column(name = "policy_id", columnDefinition = "UUID")
    private UUID policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private TripHistoryStatus eventType;

    @Column(name = "status_before", nullable = false, length = 50)
    private String statusBefore;

    @Column(name = "status_after", nullable = false, length = 50)
    private String statusAfter;


}
