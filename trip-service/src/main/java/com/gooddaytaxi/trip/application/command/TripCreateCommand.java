package com.gooddaytaxi.trip.application.command;

import java.util.UUID;

public record TripCreateCommand(
        UUID policyId,
        UUID passengerId,
        UUID driverId,
        String pickupAddress,
        String destinationAddress
) {
}
