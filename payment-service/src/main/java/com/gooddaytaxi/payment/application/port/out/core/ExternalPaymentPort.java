package com.gooddaytaxi.payment.application.port.out.core;

import com.gooddaytaxi.payment.application.command.payment.ExternalPaymentConfirmCommand;
import com.gooddaytaxi.payment.application.command.refund.ExternalPaymentCancelCommand;
import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentConfirmResult;
import com.gooddaytaxi.payment.application.result.refund.ExternalPaymentCancelResult;

import java.util.UUID;

public interface ExternalPaymentPort {
    ExternalPaymentConfirmResult confirm(String idempotencyKey, ExternalPaymentConfirmCommand command);

    ExternalPaymentCancelResult cancelTosspayPayment(String paymentKey, UUID idempotencyKey, ExternalPaymentCancelCommand command);

    ExternalPaymentConfirmResult getPayment(String paymentKey);
}
