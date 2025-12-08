package com.gooddaytaxi.trip.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripStartResponse(
        UUID tripId,
        String status,
        LocalDateTime startTime,
        String message
) {
}
