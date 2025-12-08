package com.gooddaytaxi.trip.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripEndResponse(
        UUID tripId,
        String status,
        LocalDateTime endTime,
        Long finalFare,
        String message
) {
}
