package com.gooddaytaxi.trip.application.command;

import java.util.UUID;

public record UpdateTripLocationCommand(
        UUID tripId,
        UUID notifierId,
        String currentAddress,
        String region
) {
}
