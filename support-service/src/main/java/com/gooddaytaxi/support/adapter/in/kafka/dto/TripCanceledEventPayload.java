package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TRIP_CANCELED 이벤트 Payload DTO
 */
public record TripCanceledEventPayload(
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID tripId,
        UUID driverId,
        UUID passengerId,
        String cancelReason,
        LocalDateTime canceledAt
) {
}
