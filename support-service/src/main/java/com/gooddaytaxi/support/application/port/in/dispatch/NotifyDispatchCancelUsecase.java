package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.input.dispatch.NotifyDispatchCanceledCommand;

public interface NotifyDispatchCancelUsecase {
    void execute(NotifyDispatchCanceledCommand command);
}
