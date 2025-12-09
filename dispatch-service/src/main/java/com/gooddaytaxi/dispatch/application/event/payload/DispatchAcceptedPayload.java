package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchAcceptedPayload(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        LocalDateTime acceptedAt
) {
    public static DispatchAcceptedPayload from(Dispatch dispatch, UUID driverId) {
        return new DispatchAcceptedPayload(
                dispatch.getDispatchId(),
                driverId,
                dispatch.getPassengerId(),
                LocalDateTime.now()
        );
    }
}
