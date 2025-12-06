package com.gooddaytaxi.payment.application.result.refund;

import java.util.UUID;

public record RefundCreateResult(UUID paymentId, String message) {
}
