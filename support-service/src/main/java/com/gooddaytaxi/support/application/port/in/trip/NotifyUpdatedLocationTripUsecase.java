package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.trip.TripLocationUpdatedCommand;

public interface NotifyUpdatedLocationTripUsecase {
    void execute(TripLocationUpdatedCommand command);
}
