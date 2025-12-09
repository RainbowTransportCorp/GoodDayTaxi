package com.gooddaytaxi.trip.application.result;

import com.gooddaytaxi.trip.domain.model.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripHistoryItem(
        UUID tripId,
        String status,
        String pickupAddress,
        String destinationAddress,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal totalDistance,
        long totalDuration,
        long finalFare
) {

    public static TripHistoryItem from(Trip trip) {
        return new TripHistoryItem(
                trip.getTripId(),
                trip.getStatus().name(),          // enum -> String
                trip.getPickupAddress(),
                trip.getDestinationAddress(),
                trip.getStartTime(),
                trip.getEndTime(),
                trip.getTotalDistance(),
                trip.getTotalDuration(),          // Trip 엔티티에 있는 필드 기준
                trip.getFinalFare()
        );
    }
}
