package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchCanceledPayload;

public interface DispatchCanceledCommandPort {
    void publishCanceled(DispatchCanceledPayload payload);
}
