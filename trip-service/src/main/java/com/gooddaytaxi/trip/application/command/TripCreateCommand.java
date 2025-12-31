package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.application.validator.UserRole;
import java.util.UUID;

public record TripCreateCommand(
    UUID masterId,
    UserRole role,
    UUID policyId,
    UUID passengerId,
    UUID driverId,
    UUID dispatchId,
    String pickupAddress,
    String destinationAddress
) {

}
