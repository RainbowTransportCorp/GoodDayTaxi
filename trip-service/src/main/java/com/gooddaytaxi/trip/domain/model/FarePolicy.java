package com.gooddaytaxi.trip.domain.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.trip.domain.model.enums.PolicyType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name="p_fare_policies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FarePolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "policy_id", nullable = false, updatable = false)
    private UUID policyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 50)
    private PolicyType policyType;

    @Column(name = "base_distance", nullable = false)
    private Double baseDistance; // 기본 거리(km)

    @Column(name = "base_fare", nullable = false)
    private Long baseFare; // 기본 요금(원)

    @Column(name = "dist_rate_km", nullable = false)
    private Long distRateKm; // 거리당 요금(원/km)

    @Column(name = "time_rate", nullable = false)
    private Long timeRate; // 시간당 요금(원/분)

    @Column(nullable = false)
    private boolean isActive;



    @Builder
    private FarePolicy(
            PolicyType policyType, Double baseDistance,
            Long baseFare, Long distRateKm, Long timeRate) {

        this.policyType = policyType;
        this.baseDistance = baseDistance;
        this.baseFare = baseFare;
        this.distRateKm = distRateKm;
        this.timeRate = timeRate;
        this.isActive = true;

    }

    public static FarePolicy create(
            PolicyType policyType, Double baseDistance, Long baseFare,
            Long distRateKm, Long timeRate) {


        return FarePolicy.builder()
                .policyType(policyType)
                .baseDistance(baseDistance)
                .baseFare(baseFare)
                .distRateKm(distRateKm)
                .timeRate(timeRate)
                .build();
    }

    public void update(PolicyType policyType, Double baseDistance, Long baseFare,
                       Long distRateKm, Long timeRate) {

        if (baseFare < 0) {
            throw new IllegalArgumentException("기본 요금은 음수일 수 없습니다.");
        }

        if (distRateKm < 0 || timeRate < 0) {
            throw new IllegalArgumentException("추가 요금은 음수일 수 없습니다.");
        }

        this.policyType = policyType;
        this.baseDistance = baseDistance;
        this.baseFare = baseFare;
        this.distRateKm = distRateKm;
        this.timeRate = timeRate;
    }


}
