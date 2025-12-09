package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRequestedPayload;

public interface DispatchRequestedCommandPort {
    void publishRequested(DispatchRequestedPayload payload);
}