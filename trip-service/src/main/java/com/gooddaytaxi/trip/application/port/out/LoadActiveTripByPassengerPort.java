package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;
import java.util.Optional;
import java.util.UUID;

public interface LoadActiveTripByPassengerPort {
    Optional<Trip> loadActiveTripByPassengerId(UUID passengerId);
}

