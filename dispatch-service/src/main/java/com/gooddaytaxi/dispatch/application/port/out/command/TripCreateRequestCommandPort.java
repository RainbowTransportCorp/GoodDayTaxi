package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;

public interface TripCreateRequestCommandPort {
    void publishTripCreateRequest (TripCreateRequestPayload payload);
}
