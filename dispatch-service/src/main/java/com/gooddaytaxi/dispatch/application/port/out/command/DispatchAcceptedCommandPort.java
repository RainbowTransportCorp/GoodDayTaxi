package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;

public interface DispatchAcceptedCommandPort {
    void publishAccepted(DispatchAcceptedPayload payload);
}
