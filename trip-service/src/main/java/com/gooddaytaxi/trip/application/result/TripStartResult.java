package com.gooddaytaxi.trip.application.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripStartResult(
        UUID tripId,
        String status,
        LocalDateTime startTime,
        String message
) {
}
