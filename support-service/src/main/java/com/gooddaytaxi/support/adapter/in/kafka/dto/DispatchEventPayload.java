package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.util.UUID;

/**
 * DISPATCH_REQUESTED, DISPATCH_ACCEPTED 이벤트 Payload DTO
 */
public record DispatchEventPayload(
    String message,
    UUID driverId,
    UUID passengerId,
    UUID notifierId,
    UUID notificationOriginId,
    String pickupAddress,
    String destinationAddress
) {}