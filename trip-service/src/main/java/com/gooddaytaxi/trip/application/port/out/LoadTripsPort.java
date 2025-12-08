package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;

import java.util.List;

public interface LoadTripsPort {
    List<Trip> findAll();
}
