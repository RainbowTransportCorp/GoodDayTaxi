package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.trip.TripEndedCommand;

public interface NotifyEndedTripUsecase {
    void execute(TripEndedCommand command);
}
