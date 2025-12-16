package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.NotifyDispatchRejectedCommand;

public interface NotifyDispatchRejectUsecase {
    void execute(NotifyDispatchRejectedCommand command);
}
