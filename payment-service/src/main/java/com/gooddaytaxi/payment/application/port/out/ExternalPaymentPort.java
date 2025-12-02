package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.application.command.ExternalPaymentConfirmCommand;
import com.gooddaytaxi.payment.application.result.ExternalPaymentConfirmResult;

public interface ExternalPaymentPort {
    ExternalPaymentConfirmResult confirm(String idempotencyKey, ExternalPaymentConfirmCommand command);
}
