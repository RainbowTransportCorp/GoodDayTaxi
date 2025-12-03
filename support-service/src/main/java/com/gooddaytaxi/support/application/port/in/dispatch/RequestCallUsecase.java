package com.gooddaytaxi.support.application.port.in.dispatch;

import com.gooddaytaxi.support.application.dto.CreateCallCommand;

/**
 * 콜 요청 Usecase
 */
public interface RequestCallUsecase {
    void request(CreateCallCommand command);
    // void cancel(DeleteCallCommand command);
}
