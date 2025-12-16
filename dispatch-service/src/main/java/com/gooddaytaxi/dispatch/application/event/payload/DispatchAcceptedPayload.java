package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchAcceptedPayload(
        UUID notificationOriginId,   // = dispatchId
        UUID notifierId,             // = driverId
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        String message,
        LocalDateTime acceptedAt
) {
    public static DispatchAcceptedPayload from(Dispatch dispatch, UUID driverId) {
        return new DispatchAcceptedPayload(
                dispatch.getDispatchId(),
                driverId,
                dispatch.getDispatchId(),
                driverId,
                dispatch.getPassengerId(),
                dispatch.getPickupAddress(),
                dispatch.getDestinationAddress(),
                "배차가 완료되었습니다.",
                LocalDateTime.now()
        );
    }
}
