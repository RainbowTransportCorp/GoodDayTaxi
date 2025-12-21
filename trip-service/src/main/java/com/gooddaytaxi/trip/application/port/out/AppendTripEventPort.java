package com.gooddaytaxi.trip.application.port.out;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface AppendTripEventPort {

    UUID appendTripStarted(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String pickupAddress,
            String destinationAddress,
            LocalDateTime startTime
    );


    UUID appendTripEnded(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String pickupAddress,
            String destinationAddress,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BigDecimal totalDistance,
            Long totalDuration,
            Long finalFare
    );


    UUID appendTripCanceled(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String cancelReason,
            LocalDateTime canceledAt
    );



    UUID appendTripLocationUpdated(
            UUID tripId,
            UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            String currentAddress,
            String region,
            String previousRegion,
            Long sequence,
            LocalDateTime locationTime
    );

}
