package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.application.command.TripCreateRequestCommand;
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

    @Column(name = "dispatch_id", nullable = false, unique = true)
    private UUID dispatchId;

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
            UUID dispatchId,
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
        this.dispatchId = dispatchId;
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
            UUID dispatchId,
            String pickupAddress,
            String destinationAddress
    ) {
        LocalDateTime now = LocalDateTime.now();

        return Trip.builder()
                .policyId(policyId)
                .passengerId(passengerId)
                .driverId(driverId)
                .dispatchId(dispatchId)      // ✅ dispatch_id NOT NULL 해결 핵심
                .status(TripStatus.READY)
                .startTime(now)
                .endTime(now)
                .pickupAddress(pickupAddress)
                .destinationAddress(destinationAddress)
                .totalDuration(0L)
                .totalDistance(BigDecimal.ZERO)
                .finalFare(0L)
                .build();
    }

    // Dispatch 이벤트 기반 운행 생성 (의미 기반 팩토리)
    public static Trip createFromDispatch(
            FarePolicy policy,
            TripCreateRequestCommand command
    ) {
        return Trip.builder()
                .policyId(policy.getPolicyId())
                .passengerId(command.passengerId())
                .driverId(command.driverId())
                .dispatchId(command.dispatchId())
                .status(TripStatus.READY)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .pickupAddress(command.pickupAddress())
                .destinationAddress(command.destinationAddress())
                .totalDuration(0L)
                .totalDistance(BigDecimal.ZERO)
                .finalFare(0L)
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

    public void end(BigDecimal totalDistance, long totalDuration) {
        if (this.status != TripStatus.STARTED) {
            throw new IllegalStateException("STARTED 상태에서만 ENDED로 변경할 수 있습니다.");
        }

        this.status = TripStatus.ENDED;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.endTime = LocalDateTime.now();

        // 요금 계산 로직 (임시 간단 버전)
        this.finalFare = calculateFare(totalDistance, totalDuration);
    }

    private long calculateFare(BigDecimal totalDistance, long totalDurationSeconds) {
        // 예시 정책: 기본 3,000원 + (km당 1,000원) + (초당 2원)
        long baseFare = 3000L;
        long distanceFare = totalDistance
                .multiply(BigDecimal.valueOf(1000L))
                .longValue();
        long timeFare = totalDurationSeconds * 2L;

        return baseFare + distanceFare + timeFare;
    }
//나중에  FarePolicy 적용할 때는 calculateFare 부분 리펙토링
}
