package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface LoadTripsByDriverPort {
    Page<Trip> loadTripsByDriverId(UUID driverId, int page, int size);
}
