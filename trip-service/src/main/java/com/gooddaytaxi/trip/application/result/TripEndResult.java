package com.gooddaytaxi.trip.application.result;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripEndResult(
        UUID tripId,
        String status,
        LocalDateTime endTime,
        Long finalFare,
        String message
) {
}
