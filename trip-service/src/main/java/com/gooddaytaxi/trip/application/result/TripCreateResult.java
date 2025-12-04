package com.gooddaytaxi.trip.application.result;

import com.gooddaytaxi.trip.domain.model.enums.TripStatus;

import java.util.UUID;

public record TripCreateResult(
        UUID tripId,
        String status,
        String message
) {
}
