package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.DispatchAcceptCommand;

public interface AcceptDispatchUsecase {
    void handle(DispatchAcceptCommand event);
}
