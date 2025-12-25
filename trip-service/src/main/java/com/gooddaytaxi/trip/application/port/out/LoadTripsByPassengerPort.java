package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoadTripsByPassengerPort {

    PassengerTripsPage loadTripsByPassengerId(UUID passengerId, int page, int size);

    record PassengerTripsPage(
            List<Trip> trips,
            long totalCount
    ) {
    }

    List<Trip> findAllByPassengerId(UUID passengerId);

}
