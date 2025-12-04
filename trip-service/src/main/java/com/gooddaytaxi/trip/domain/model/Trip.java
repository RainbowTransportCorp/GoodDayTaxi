package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name="p_trips")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trip_id")
    private UUID tripId;

    // 실제로 INSERT/UPDATE 되는 컬럼
    @Column(name = "policy_id", nullable = false)
    private UUID policyId;

    // 읽기 전용 연관관계 (객체 그래프 조회용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", insertable = false, updatable = false)
    private FarePolicy farePolicy;

    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "pickup_address", length = 255)
    private String pickupAddress;

    @Column(name = "destination_address", length = 255)
    private String destinationAddress;

    @Column(name = "total_duration")
    private Long totalDuration; // minutes

    @Column(name = "total_distance", precision = 15, scale = 2)
    private BigDecimal totalDistance; // km

    @Column(name = "final_fare")
    private Long finalFare;
}
