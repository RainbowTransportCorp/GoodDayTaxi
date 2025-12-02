package com.gooddaytaxi.trip.presentation.dto.response;

import java.util.UUID;

public record FarePolicyResponse(
        UUID policyId,
        String policyType,
        String message
) {}



