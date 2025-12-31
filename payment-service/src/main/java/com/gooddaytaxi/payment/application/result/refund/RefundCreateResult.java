package com.gooddaytaxi.payment.application.result.refund;

import com.gooddaytaxi.payment.application.message.SuccessMessage;

import java.util.UUID;

public record RefundCreateResult(UUID paymentId, SuccessMessage message) {
}
