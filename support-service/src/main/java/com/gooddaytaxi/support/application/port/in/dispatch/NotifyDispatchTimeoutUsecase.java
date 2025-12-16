package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.NotifyDipsatchTimeoutCommand;

public interface NotifyDispatchTimeoutUsecase {
    void execute(NotifyDipsatchTimeoutCommand command);

}
