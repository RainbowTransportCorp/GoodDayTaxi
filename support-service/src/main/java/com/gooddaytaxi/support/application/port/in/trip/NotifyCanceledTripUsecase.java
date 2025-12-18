package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.trip.TripCanceledCommand;

public interface NotifyCanceledTripUsecase {
    void execute(TripCanceledCommand command);
}
