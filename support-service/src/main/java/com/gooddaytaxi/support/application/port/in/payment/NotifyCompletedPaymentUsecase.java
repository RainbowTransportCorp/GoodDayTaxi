package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.payment.NotifyPaymentCompletedCommand;

public interface NotifyCompletedPaymentUsecase {
    void execute(NotifyPaymentCompletedCommand command);
}
