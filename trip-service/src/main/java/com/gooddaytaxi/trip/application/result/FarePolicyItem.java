package com.gooddaytaxi.trip.application.result;

import com.gooddaytaxi.trip.domain.model.enums.PolicyType;

import java.util.UUID;

public record FarePolicyItem(
        UUID policyId,
        PolicyType policyType,
        double baseDistance,
        long baseFare,
        long distRateKm,
        long timeRate
) {
}
