package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchTimeoutPayload(
        UUID dispatchId,
        UUID passengerId,
        LocalDateTime timeoutAt
) {

    public static DispatchTimeoutPayload from(
            UUID dispatchId, UUID passengerI, LocalDateTime timeoutAt) {
        return new DispatchTimeoutPayload(
                dispatchId,
                passengerI,
                timeoutAt);
    }
}
