package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.input.dispatch.NotifyDipsatchForceTimeoutCommand;

public interface NotifyDispatchForceTimeoutUsecase {
    void execute(NotifyDipsatchForceTimeoutCommand command);
}
