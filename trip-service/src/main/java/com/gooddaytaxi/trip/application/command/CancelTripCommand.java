package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.domain.model.enums.CancelReason;

import java.util.UUID;

public record CancelTripCommand(
        UUID tripId,
        UUID notifierId,
        CancelReason cancelReason
) {
}
