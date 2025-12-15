package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.LoadTripsByDriverPort;
import com.gooddaytaxi.trip.application.port.out.LoadTripsPort;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.infrastructure.persistence.TripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripQueryAdapter implements LoadTripsPort, LoadTripsByDriverPort {

    private final TripJpaRepository tripJpaRepository;

    @Override
    public List<Trip> findAll() {     // ✅ findAll (L 하나)
        return tripJpaRepository.findAll();
    }

    @Override
    public Page<Trip> loadTripsByDriverId(UUID driverId, int page, int size) {
        return tripJpaRepository.findByDriverIdOrderByStartTimeDesc(
                driverId,
                PageRequest.of(page, size)
        );
    }
}
