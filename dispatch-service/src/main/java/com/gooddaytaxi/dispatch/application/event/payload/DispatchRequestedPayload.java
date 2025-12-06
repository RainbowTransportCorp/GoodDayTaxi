package com.gooddaytaxi.dispatch.application.event.payload;


import java.util.UUID;

public record DispatchRequestedPayload(
    UUID notificationOriginId,
    UUID notifierId,
    UUID driverId,
    UUID passengerId,
    String pickupAddress,
    String destinationAddress,
    String message
) {
    public static DispatchRequestedPayload from(
        UUID dispatchId,
        UUID passengerId,
        UUID driverId,
        String pickupAddress,
        String destinationAddress,
        String message
    ) {
        return new DispatchRequestedPayload(
            dispatchId,
            passengerId,
            driverId,
            passengerId,
            pickupAddress,
            destinationAddress,
            message
        );
    }
}

