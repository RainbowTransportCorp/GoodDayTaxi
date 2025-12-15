package com.gooddaytaxi.support.application.port.in.trip;

import com.gooddaytaxi.support.application.dto.NotifyTripEndedCommand;

public interface NotifyEndedTripUsecase {
    void execute(NotifyTripEndedCommand command);
}
