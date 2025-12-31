package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import java.time.LocalDateTime;

import java.util.UUID;

public record TripCreateRequestPayload(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime assignedAt
) {
    public static TripCreateRequestPayload from(Dispatch dispatch) {
        return new TripCreateRequestPayload(
                dispatch.getDispatchId(),
                dispatch.getDriverId(),
                dispatch.getPassengerId(),
                dispatch.getPickupAddress(),
                dispatch.getDestinationAddress(),
                dispatch.getAssignedAt()
        );
    }
}

