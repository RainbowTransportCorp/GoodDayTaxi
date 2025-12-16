package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.dispatch.NotifyDispatchAcceptedCommand;

public interface NotifyAcceptedCallUsecase {
    void execute(NotifyDispatchAcceptedCommand command);
}
