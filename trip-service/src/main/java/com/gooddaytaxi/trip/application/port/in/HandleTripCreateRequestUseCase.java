package com.gooddaytaxi.trip.application.port.in;

import com.gooddaytaxi.trip.application.command.TripCreateRequestCommand;

public interface HandleTripCreateRequestUseCase {

    void handle(TripCreateRequestCommand command);
}
