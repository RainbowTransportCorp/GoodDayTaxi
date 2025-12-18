package com.gooddaytaxi.support.application.port.in.monitoring;

import com.gooddaytaxi.support.application.dto.log.NotifyErrorLogCommand;

public interface NotifyErrorDetectedUsecase {
    void execute(NotifyErrorLogCommand command);

}
