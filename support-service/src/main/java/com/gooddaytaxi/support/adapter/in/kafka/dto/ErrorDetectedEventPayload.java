package com.gooddaytaxi.support.adapter.in.kafka.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * ERROR_DETECTED 이벤트 Payload DTO
 */
public record ErrorDetectedEventPayload(
        @NotNull UUID notificationOriginId,
        @NotNull UUID notifierId,
        UUID dispatchId,
        UUID tripId,
        UUID paymentId,
        UUID driverId,
        UUID passengerId,
        String sourceNotificationType,
        @NotNull String logType,                 // DISPATCH_ERROR, TRIP_ERROR, PAYMENT_ERROR, SYSTEM_ERROR
        String logMessage
) {
}
