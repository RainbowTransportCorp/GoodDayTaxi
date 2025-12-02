package com.gooddaytaxi.payment.application.result;

import java.util.UUID;

public record PaymentApproveResult(UUID paymentId,
                                   Long amount,
                                   String status,
                                   String method) {
}
