package com.gooddaytaxi.trip.infrastructure.persistence;

import com.gooddaytaxi.trip.domain.model.Trip;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripJpaRepositoryCustom {
    Optional<Trip> findActiveByDriverId(UUID driverId);
    Optional<Trip> findActiveByPassengerId(UUID passengerId);

    Optional<Trip> findByTripIdAndPassengerId(UUID tripId, UUID passengerId);
    Optional<Trip> findByTripIdAndDriverId(UUID tripId, UUID driverId);

    List<Trip> findAllByPassengerId(UUID passengerId);

    List<Trip> findAllByDriverId(UUID driverId);
}
