package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchForceTimeoutPayload;

public interface DispatchForceTimeoutCommandPort {
    void publish(DispatchForceTimeoutPayload payload);
}
