package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.application.result.payment.ExternalPaymentError;

import java.util.UUID;

public record TossPayRefundCancelFailedAfterRollbackEvent(
        UUID paymentId,
        RefundSnapshot refund,
        ExternalPaymentError error
) {}
