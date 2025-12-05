package com.gooddaytaxi.trip.presentation.dto.response;

import com.gooddaytaxi.trip.domain.model.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripResponse(
        UUID tripId,
        String status,
        UUID policyId,
        UUID passengerId,
        UUID driverId,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal totalDistance,
        Long finalFare
) { }
