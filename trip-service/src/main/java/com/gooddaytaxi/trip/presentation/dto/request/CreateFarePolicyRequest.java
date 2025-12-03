package com.gooddaytaxi.trip.presentation.dto.request;

import com.gooddaytaxi.trip.domain.model.enums.PolicyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateFarePolicyRequest(
        @NotNull(message = "정책 타입은 필수입니다.")
        String policyType,

        @NotNull(message = "기본 거리는 필수입니다.")
        @Min(value = 0, message = "기본 거리는 0보다 크거나 같아야 합니다.")
        Double baseDistance,

        @NotNull(message = "기본 요금은 필수입니다.")
        @Min(value = 0, message = "기본 요금은 0원 이상이어야 합니다.")
        Long baseFare,

        @NotNull(message = "거리당 추가 요금은 필수입니다.")
        @Min(value = 0, message = "거리당 요금은 0원 이상이어야 합니다.")
        Long distRateKm,

        @NotNull(message = "시간당 추가 요금은 필수입니다.")
        @Min(value = 0, message = "시간당 요금은 0원 이상이어야 합니다.")
        Long timeRate
) {
}
