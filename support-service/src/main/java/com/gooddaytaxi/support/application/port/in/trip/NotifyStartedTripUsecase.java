package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.trip.TripStartedCommand;

public interface NotifyStartedTripUsecase {
    void execute(TripStartedCommand command);
}
