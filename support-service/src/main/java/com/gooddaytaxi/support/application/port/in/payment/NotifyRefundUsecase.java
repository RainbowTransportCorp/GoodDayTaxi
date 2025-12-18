package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.payment.RefundCompletedCommand;
import com.gooddaytaxi.support.application.dto.payment.RefundRejectedCommand;
import com.gooddaytaxi.support.application.dto.payment.RefundRequestedCommand;
import com.gooddaytaxi.support.application.dto.payment.RefundSettlementCreatedCommand;

public interface NotifyRefundUsecase {
    void request(RefundRequestedCommand command);
    void reject(RefundRejectedCommand command);
    void complete(RefundCompletedCommand command);
    void createSettlement(RefundSettlementCreatedCommand command);
}
