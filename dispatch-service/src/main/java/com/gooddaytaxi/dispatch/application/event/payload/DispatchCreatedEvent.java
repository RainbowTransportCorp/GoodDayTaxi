package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DispatchCreatedEvent(
        UUID dispatchId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime requestCreatedAt
) {
    public static DispatchCreatedEvent from(Dispatch d) {
        return new DispatchCreatedEvent(
                d.getDispatchId(),
                d.getPassengerId(),
                d.getPickupAddress(),
                d.getDestinationAddress(),
                d.getRequestCreatedAt()
        );
    }
}
