package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.application.command.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.result.PaymentConfirmResult;

public interface TosspayClient {
    PaymentConfirmResult confirmPayment(String idempotencyKey, PaymentTossPayCommand command);
}
