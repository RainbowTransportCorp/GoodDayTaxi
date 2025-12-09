package com.gooddaytaxi.dispatch.application.port.out.command;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;

public interface DispatchCanceledCommandPort {
    void publishCanceled(DispatchRejectedPayload payload);
}
