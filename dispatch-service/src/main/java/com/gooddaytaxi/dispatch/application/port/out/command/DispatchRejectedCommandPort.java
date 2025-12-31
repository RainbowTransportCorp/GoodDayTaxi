package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;

public interface DispatchRejectedCommandPort {
    void publishRejected(DispatchRejectedPayload payload);
}
