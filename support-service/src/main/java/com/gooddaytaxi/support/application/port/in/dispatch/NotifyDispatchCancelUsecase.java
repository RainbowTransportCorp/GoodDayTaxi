package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.dispatch.NotifyDispatchCancelledCommand;

public interface NotifyDispatchCancelUsecase {
    void execute(NotifyDispatchCancelledCommand command);
}
