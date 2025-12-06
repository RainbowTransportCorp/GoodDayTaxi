package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;


public record DispatchCreatedPayload(
        UUID dispatchId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime requestCreatedAt
) {
    public static DispatchCreatedPayload from(Dispatch d) {
        return new DispatchCreatedPayload(
                d.getDispatchId(),
                d.getPassengerId(),
                d.getPickupAddress(),
                d.getDestinationAddress(),
                d.getRequestCreatedAt()
        );
    }
}
