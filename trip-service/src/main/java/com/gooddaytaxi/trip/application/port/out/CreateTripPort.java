package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;

public interface CreateTripPort {
    Trip createTrip(Trip trip);
}
