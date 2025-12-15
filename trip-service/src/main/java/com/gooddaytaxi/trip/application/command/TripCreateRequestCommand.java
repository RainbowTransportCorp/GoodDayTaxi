package com.gooddaytaxi.trip.application.command;

import com.gooddaytaxi.trip.infrastructure.messaging.model.TripCreateRequestPayload;

import java.time.LocalDateTime;
import java.util.UUID;

public record TripCreateRequestCommand(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime assignedAt
) {
    public static TripCreateRequestCommand from(TripCreateRequestPayload payload) {
        return new TripCreateRequestCommand(
                payload.dispatchId(),
                payload.driverId(),
                payload.passengerId(),
                payload.pickupAddress(),
                payload.destinationAddress(),
                payload.assignedAt()
        );
    }

}
