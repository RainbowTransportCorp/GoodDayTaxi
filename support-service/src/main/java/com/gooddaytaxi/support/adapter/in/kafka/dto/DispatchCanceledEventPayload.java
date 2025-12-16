package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_CANCELLED 이벤트 Payload DTO
 */
public record DispatchCanceledEventPayload(
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String canceledBy,          // PASSENGER or SYSTEM
        String message,
        LocalDateTime canceledAt

) {
}
