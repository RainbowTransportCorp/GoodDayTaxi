package com.gooddaytaxi.payment.application.result.payment;

import java.util.UUID;

public record PaymentUpdateResult(UUID paymentId,
                                  Long amount,
                                  String method) {
}
