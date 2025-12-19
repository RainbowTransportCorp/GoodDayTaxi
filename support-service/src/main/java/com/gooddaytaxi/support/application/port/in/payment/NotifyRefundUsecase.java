package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.input.payment.RefundCompletedCommand;
import com.gooddaytaxi.support.application.dto.input.payment.RefundRejectedCommand;
import com.gooddaytaxi.support.application.dto.input.payment.RefundRequestedCommand;
import com.gooddaytaxi.support.application.dto.input.payment.RefundSettlementCreatedCommand;

public interface NotifyRefundUsecase {
    void request(RefundRequestedCommand command);
    void reject(RefundRejectedCommand command);
    void complete(RefundCompletedCommand command);
    void createSettlement(RefundSettlementCreatedCommand command);
}
