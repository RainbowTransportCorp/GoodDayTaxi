package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.input.trip.TripLocationUpdatedCommand;

public interface NotifyTripLocationUpdateUsecase {
    void execute(TripLocationUpdatedCommand command);
}
