package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.payment.NotifyRefundCompletedCommand;
import com.gooddaytaxi.support.application.dto.payment.NotifyRefundRejectedCommand;
import com.gooddaytaxi.support.application.dto.payment.NotifyRefundRequestedCommand;
import com.gooddaytaxi.support.application.dto.payment.NotifyRefundSettlementCreatedCommand;

public interface NotifyRefundUsecase {
    void request(NotifyRefundRequestedCommand command);
    void reject(NotifyRefundRejectedCommand command);
    void complete(NotifyRefundCompletedCommand command);
    void createSettlement(NotifyRefundSettlementCreatedCommand command);
}
