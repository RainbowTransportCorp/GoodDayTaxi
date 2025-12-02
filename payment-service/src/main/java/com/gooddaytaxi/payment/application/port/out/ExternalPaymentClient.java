package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.application.command.PaymentTossPayCommand;
import com.gooddaytaxi.payment.application.result.ExternalPaymentConfirmResult;

public interface ExternalPaymentClient {
    ExternalPaymentConfirmResult confirmTossPayPayment(String idempotencyKey, PaymentTossPayCommand command);
}
