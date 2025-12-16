package com.gooddaytaxi.trip.application.command;

import java.util.UUID;

public record StartTripCommand(
        UUID tripId,
        UUID notifierId
) {
}
