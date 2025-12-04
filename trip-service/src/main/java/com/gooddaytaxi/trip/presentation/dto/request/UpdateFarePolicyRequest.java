package com.gooddaytaxi.trip.presentation.dto.request;

import com.gooddaytaxi.trip.domain.model.enums.PolicyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateFarePolicyRequest(
        @NotNull PolicyType policyType,
        @NotNull @Positive Double baseDistance,
        @NotNull @Positive Long baseFare,
        @NotNull @Positive Long distRateKm,
        @NotNull @Positive Long timeRate
) {
}
