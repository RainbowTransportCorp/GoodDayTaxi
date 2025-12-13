package com.gooddaytaxi.support.adapter.in.kafka.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TRIP_ENDED 이벤트 Payload DTO
 */
public record TripEndedEventPayload(
        UUID notificationOriginId,
        UUID notifierId,
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long totalDuration,
        BigDecimal totalDistance,
        Long finalFare
) {
}
