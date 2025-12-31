package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.input.trip.TripCanceledCommand;

public interface NotifyTripCancelUsecase {
    void execute(TripCanceledCommand command);
}
