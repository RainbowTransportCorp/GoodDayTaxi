package com.gooddaytaxi.trip.application.result;

import java.util.UUID;

public record FarePolicyCreateResult(UUID policyId,
                                     String policyType,
                                     String message) {
}
