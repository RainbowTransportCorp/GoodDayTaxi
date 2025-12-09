package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.CreateDispatchInfoCommand;

/**
 * 콜 요청 Usecase
 */
public interface NotifyDispatchUsecase {
    void request(CreateDispatchInfoCommand command);
    // void cancel(DeleteCallCommand command);
}
