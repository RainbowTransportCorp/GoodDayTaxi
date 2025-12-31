package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.domain.model.enums.CancelReason;

import com.gooddaytaxi.trip.application.validator.UserRole;
import java.util.UUID;

public record CancelTripCommand(
        UUID tripId,
        UUID driverId,
        UserRole role,
        CancelReason cancelReason
) {
}
