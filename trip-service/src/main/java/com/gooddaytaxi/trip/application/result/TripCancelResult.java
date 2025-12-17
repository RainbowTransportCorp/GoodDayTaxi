package com.gooddaytaxi.trip.application.result;

import java.util.UUID;

public record TripCancelResult(
        UUID tripId,
        String status,
        String message
) {
}
