package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.payment.PaymentCompletedCommand;

public interface NotifyCompletedPaymentUsecase {
    void execute(PaymentCompletedCommand command);
}
