package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.LoadTripByIdPort;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.infrastructure.persistence.TripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
@Component
@RequiredArgsConstructor
public class TripDetailQueryAdapter implements LoadTripByIdPort {

    private final TripJpaRepository tripJpaRepository;

    @Override
    public Optional<Trip> loadTripById(UUID tripId) {
        return tripJpaRepository.findById(tripId);
    }
}
