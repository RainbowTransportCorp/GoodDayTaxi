package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchTripReadyErrorPayload;

public interface DispatchTripReadyErrorCommandPort {

    void publish(DispatchTripReadyErrorPayload payload);
}
