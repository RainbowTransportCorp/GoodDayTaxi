package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.LoadTripsPort;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.repository.TripRepository;
import com.gooddaytaxi.trip.infrastructure.persistence.TripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TripQueryAdapter implements LoadTripsPort {

    private final TripJpaRepository tripJpaRepository;

    @Override
    public List<Trip> findAll() {     // ✅ findAll (L 하나)
        return tripJpaRepository.findAll();
    }
}
