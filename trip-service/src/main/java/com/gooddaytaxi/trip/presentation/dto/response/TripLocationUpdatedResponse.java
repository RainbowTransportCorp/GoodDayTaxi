package com.gooddaytaxi.trip.presentation.dto.response;

import java.util.UUID;

public record TripLocationUpdatedResponse(
        UUID tripId,
        boolean eventPublished,
        long sequence,
        String previousRegion,
        String currentRegion
) {
}
