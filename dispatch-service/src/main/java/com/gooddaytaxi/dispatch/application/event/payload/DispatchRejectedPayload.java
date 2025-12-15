package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchRejectedPayload(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        LocalDateTime rejectedAt
) {
    public static DispatchRejectedPayload from(Dispatch dispatch) {
        return new DispatchRejectedPayload(
                dispatch.getDispatchId(),
                dispatch.getDriverId(),
                dispatch.getPassengerId(),
                LocalDateTime.now());
    }
}
