package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.application.command.payment.ExternalPaymentConfirmCommand;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;

public interface ExternalPaymentPort {
    ExternalPaymentConfirmResult confirm(String idempotencyKey, ExternalPaymentConfirmCommand command);
}
