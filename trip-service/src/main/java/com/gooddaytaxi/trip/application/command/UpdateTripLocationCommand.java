package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.application.validator.UserRole;
import java.util.UUID;

public record UpdateTripLocationCommand(
    UUID tripId,
    UUID driverId,
    UserRole role,
    String currentAddress,
    String region
) {
}
