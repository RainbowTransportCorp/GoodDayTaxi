package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    @Column(name = "trip_id", nullable = false)
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

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "pickup_address", length = 255, nullable = false)
    private String pickupAddress;

    @Column(name = "destination_address", length = 255, nullable = false)
    private String destinationAddress;

    @Column(name = "total_duration", nullable = false)
    private Long totalDuration; // minutes

    @Column(name = "total_distance", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalDistance; // km

    @Column(name = "final_fare", nullable = false)
    private Long finalFare;

    @Builder
    private Trip(
            UUID policyId,
            UUID passengerId,
            UUID driverId,
            TripStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String pickupAddress,
            String destinationAddress,
            Long totalDuration,
            BigDecimal totalDistance,
            Long finalFare
    ) {
        this.policyId = policyId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.finalFare = finalFare;
    }

    //  운행 생성용 정적 팩토리
    public static Trip createTrip(
            UUID policyId,
            UUID passengerId,
            UUID driverId,
            String pickupAddress,
            String destinationAddress
    ) {
        LocalDateTime now = LocalDateTime.now();

        return Trip.builder()
                .policyId(policyId)
                .passengerId(passengerId)
                .driverId(driverId)
                .status(TripStatus.READY)          // 초기 상태 READY
                .startTime(now)
                .endTime(now)                      // NOT NULL 방지용 기본값
                .pickupAddress(pickupAddress)
                .destinationAddress(destinationAddress)
                .totalDuration(0L)                 // 아직 운행 안 해서 0
                .totalDistance(BigDecimal.ZERO)    // 거리 0
                .finalFare(0L)                     // 요금 0
                .build();
    }


    public void start() {
        // 이미 시작되어 있으면 멱등하게 처리
        if (this.status == TripStatus.STARTED) {
            return;
        }

        // READY가 아닌 상태에서 시작 시도하면 예외
        if (this.status != TripStatus.READY) {
            throw new IllegalStateException(
                    "READY 상태에서만 STARTED로 변경할 수 있습니다. 현재 상태: " + this.status
            );
        }

        this.status = TripStatus.STARTED;

        // 시작 시간이 비어있으면 지금 시간으로
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }

}
