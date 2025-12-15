package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.NotifyTripStartedCommand;

public interface NotifyStartedTripUsecase {
    void execute(NotifyTripStartedCommand command);
}
