package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;

public interface DispatchTimeoutCommandPort {
    void publish(DispatchTimeoutPayload payload);
}
