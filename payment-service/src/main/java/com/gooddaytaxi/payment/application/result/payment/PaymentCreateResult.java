package com.gooddaytaxi.payment.application.result.payment;

import java.util.UUID;

public record PaymentCreateResult(UUID paymentId,
                                  String method,
                                  Long amount) {
}
