package com.gooddaytaxi.payment.application.result.payment;

import com.gooddaytaxi.payment.application.message.SuccessMessage;

import java.util.UUID;

public record PaymentUpdateResult(UUID paymentId,
                                  SuccessMessage message) {
}
