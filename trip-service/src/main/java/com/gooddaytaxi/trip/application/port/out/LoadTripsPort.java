package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadTripsPort {

    Optional<Trip> loadByTripIdAndPassengerId(UUID tripId, UUID passengerId);
    Optional<Trip> loadByTripIdAndDriverId(UUID tripId, UUID driverId);
}
