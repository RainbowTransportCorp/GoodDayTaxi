package com.gooddaytaxi.payment.application.result;

import java.util.UUID;

public record PaymentCreateResult(UUID paymentId,
                                  String method,
                                  Long amount) {
}
