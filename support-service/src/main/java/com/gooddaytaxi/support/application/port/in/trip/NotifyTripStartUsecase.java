package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.input.trip.TripStartedCommand;

public interface NotifyTripStartUsecase {
    void execute(TripStartedCommand command);
}
