package com.gooddaytaxi.support.application.port.in.payment;

import com.gooddaytaxi.support.application.dto.input.payment.PaymentCompletedCommand;

public interface NotifyPaymentCompleteUsecase {
    void execute(PaymentCompletedCommand command);
}
