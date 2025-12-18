package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TRIP_LOCATION_UPDATED 이벤트 Payload DTO
 */
public record TripLocationUpdatedEventPayload(
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID tripId,
        UUID driverId,
        String currentAddress,
        String region,
        String previousRegion,
        Long sequence,
        LocalDateTime locationTime
) {}
