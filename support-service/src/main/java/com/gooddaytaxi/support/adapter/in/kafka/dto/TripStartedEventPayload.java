package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TRIP_STARTED 이벤트 Payload DTO
 */
public record TripStartedEventPayload (
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID tripId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime
) { }
