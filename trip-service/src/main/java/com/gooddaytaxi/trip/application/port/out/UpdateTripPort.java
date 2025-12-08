package com.gooddaytaxi.trip.application.port.out;

import com.gooddaytaxi.trip.domain.model.Trip;

public interface UpdateTripPort {
    Trip updateTrip(Trip trip);
}
