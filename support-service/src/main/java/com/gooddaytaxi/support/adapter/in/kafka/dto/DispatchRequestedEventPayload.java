package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DISPATCH_REQUESTED 이벤트 Payload DTO
 */
public record DispatchRequestedEventPayload(
    UUID notificationOriginId,
    UUID notifierId,
    UUID driverId,
    UUID passengerId,
    String pickupAddress,
    String destinationAddress,
    String message
) {}