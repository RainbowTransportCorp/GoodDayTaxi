package com.gooddaytaxi.payment.application.result;

import java.util.UUID;

public record RefundRequestCreateResult(UUID refundRequestId, String message) {
}
