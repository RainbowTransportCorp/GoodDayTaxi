package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.GetDispatchInfoCommand;

public interface AcceptDispatchUsecase {
    void accept(GetDispatchInfoCommand command);
}
