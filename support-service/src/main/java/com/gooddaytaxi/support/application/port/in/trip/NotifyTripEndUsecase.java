package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.input.trip.TripEndedCommand;

public interface NotifyTripEndUsecase {
    void execute(TripEndedCommand command);
}
