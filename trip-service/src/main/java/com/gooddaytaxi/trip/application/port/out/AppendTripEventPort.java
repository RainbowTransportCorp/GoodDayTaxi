package com.gooddaytaxi.trip.application.port.out;

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
}
