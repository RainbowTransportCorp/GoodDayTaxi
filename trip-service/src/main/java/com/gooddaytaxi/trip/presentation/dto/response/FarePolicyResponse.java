package com.gooddaytaxi.trip.presentation.dto.response;

import java.util.UUID;

public record FarePolicyResponse(
        UUID policyId,
        String policyType,
        double baseDistance,
        long baseFare,
        long distRateKm,
        long timeRate
) {
}
