package com.gooddaytaxi.trip.presentation.dto.response;

import com.gooddaytaxi.trip.domain.model.enums.TripStatus;

import java.util.UUID;

public record CreateTripResponse(
        UUID tripId,
        String status,
        String message
) {
}
