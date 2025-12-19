package com.gooddaytaxi.support.application.port.in.monitoring;

import com.gooddaytaxi.support.application.dto.input.log.ErrorDetectedCommand;

public interface NotifyErrorDetectUsecase {
    void execute(ErrorDetectedCommand command);

}
