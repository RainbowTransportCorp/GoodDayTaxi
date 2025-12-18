package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.trip.NotifyTripCanceledCommand;

public interface NotifyCanceledTripUsecase {
    void execute(NotifyTripCanceledCommand command);
}
