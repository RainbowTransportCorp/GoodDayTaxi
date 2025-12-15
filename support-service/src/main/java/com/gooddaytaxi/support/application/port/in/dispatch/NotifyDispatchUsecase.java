package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.NotifyDispatchInformationCommand;

/**
 * 콜 요청 Usecase
 */
public interface NotifyDispatchUsecase {
    void execute(NotifyDispatchInformationCommand command);
    // void cancel(DeleteCallCommand command);
}
