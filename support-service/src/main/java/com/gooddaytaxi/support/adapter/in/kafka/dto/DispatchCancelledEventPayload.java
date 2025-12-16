package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_CANCELLED 이벤트 Payload DTO
 */
public record DispatchCancelledEventPayload(
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String cancelledBy,          // PASSENGER or SYSTEM
        LocalDateTime cancelledAt

) {
}
