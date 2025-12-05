package com.gooddaytaxi.trip.application.result;

import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripItem(UUID tripId,
                       String status,
                       UUID policyId,
                       UUID passengerId,
                       UUID driverId,
                       String pickupAddress,
                       String destinationAddress,
                       LocalDateTime startTime,
                       LocalDateTime endTime,
                       BigDecimal totalDistance,
                       Long finalFare)
{
    public static TripItem from(Trip trip) {
        return new TripItem(
                trip.getTripId(),
                trip.getStatus().name(),
                trip.getPolicyId(),
                trip.getPassengerId(),
                trip.getDriverId(),
                trip.getPickupAddress(),
                trip.getDestinationAddress(),
                trip.getStartTime(),
                trip.getEndTime(),
                trip.getTotalDistance(),
                trip.getFinalFare()
        );
    }
}
