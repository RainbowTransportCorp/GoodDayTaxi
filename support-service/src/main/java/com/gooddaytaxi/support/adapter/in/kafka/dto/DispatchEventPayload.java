package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_REQUESTED, DISPATCH_ACCEPTED 이벤트 Payload DTO
 */
public record DispatchEventPayload(
    UUID notificationOriginId,
    UUID notifierId,
    UUID dispatchId,
    UUID driverId,
    UUID passengerId,
    String pickupAddress,
    String destinationAddress,
    String message,
    LocalDateTime acceptedAt
) {}