package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.NotifyRefundCompletedCommand;
import com.gooddaytaxi.support.application.dto.NotifyRefundRejectedCommand;
import com.gooddaytaxi.support.application.dto.NotifyRefundRequestedCommand;
import com.gooddaytaxi.support.application.dto.NotifyRefundSettlementCreatedCommand;

public interface NotifyRefundUsecase {
    void request(NotifyRefundRequestedCommand command);
    void reject(NotifyRefundRejectedCommand command);
    void complete(NotifyRefundCompletedCommand command);
    void createSettlement(NotifyRefundSettlementCreatedCommand command);
}
