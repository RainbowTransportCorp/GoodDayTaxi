package com.gooddaytaxi.dispatch.application.event.payload;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchRejectedPayload(
        UUID dispatchId,
        UUID driverId,
        LocalDateTime rejectedAt
) {
    public static DispatchRejectedPayload from(UUID dispatchId, UUID driverId, LocalDateTime rejectedAt) {
        return new DispatchRejectedPayload(dispatchId, driverId, rejectedAt);
    }
}
