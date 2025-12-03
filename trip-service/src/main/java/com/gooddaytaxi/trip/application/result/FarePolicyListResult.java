package com.gooddaytaxi.trip.application.result;

import java.util.List;

public record FarePolicyListResult(
        List<FarePolicyItem> policies
) {
}
