package com.gooddaytaxi.payment.application.result.payment;

import java.util.UUID;

public record PaymentApproveResult(UUID paymentId,
                                   Long amount,
                                   String status,
                                   String method) {
}
