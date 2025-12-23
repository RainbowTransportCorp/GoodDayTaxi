package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;
import java.util.Optional;
import java.util.UUID;

public interface LoadActiveTripByDriverPort {
    Optional<Trip> loadActiveTripByDriverId(UUID driverId);
}
