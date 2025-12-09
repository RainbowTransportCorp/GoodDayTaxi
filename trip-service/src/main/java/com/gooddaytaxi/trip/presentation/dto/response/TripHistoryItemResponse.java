package com.gooddaytaxi.trip.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripHistoryItemResponse(
        String tripId,
        String status,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal totalDistance,
        long totalDuration,
        long finalFare
)
{
}
