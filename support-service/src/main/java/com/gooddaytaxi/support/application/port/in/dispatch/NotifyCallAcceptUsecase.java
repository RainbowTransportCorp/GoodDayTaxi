package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.input.dispatch.NotifyDispatchAcceptedCommand;

public interface NotifyCallAcceptUsecase {
    void execute(NotifyDispatchAcceptedCommand command);
}
