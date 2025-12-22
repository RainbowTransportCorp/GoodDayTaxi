package com.gooddaytaxi.trip.infrastructure.persistence;

import com.gooddaytaxi.trip.domain.model.Trip;
import java.util.Optional;
import java.util.UUID;

public interface TripJpaRepositoryCustom {
    Optional<Trip> findActiveByDriverId(UUID driverId);
    Optional<Trip> findActiveByPassengerId(UUID passengerId);
}
