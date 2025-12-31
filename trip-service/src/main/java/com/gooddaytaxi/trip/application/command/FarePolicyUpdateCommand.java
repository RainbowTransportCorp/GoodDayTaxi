package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.domain.model.enums.PolicyType;

public record FarePolicyUpdateCommand(
        PolicyType policyType,
        Double baseDistance,
        Long baseFare,
        Long distRateKm,
        Long timeRate
) {
}
