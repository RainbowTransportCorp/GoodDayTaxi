package com.gooddaytaxi.support.application.port.in.monitoring;

import com.gooddaytaxi.support.application.dto.log.ErrorLogCommand;

public interface NotifyErrorDetectedUsecase {
    void execute(ErrorLogCommand command);

}
