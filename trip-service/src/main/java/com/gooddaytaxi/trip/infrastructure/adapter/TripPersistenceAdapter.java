package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.CreateTripPort;
import com.gooddaytaxi.trip.application.port.out.UpdateTripPort;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.infrastructure.persistence.TripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TripPersistenceAdapter implements CreateTripPort, UpdateTripPort {

    private final TripJpaRepository tripJpaRepository;


    @Override
    public Trip createTrip(Trip trip) {
        return tripJpaRepository.save(trip);

    }

    @Override
    public Trip updateTrip(Trip trip) {
        return tripJpaRepository.save(trip);
    }

}
