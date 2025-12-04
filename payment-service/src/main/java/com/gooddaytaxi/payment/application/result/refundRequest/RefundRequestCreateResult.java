package com.gooddaytaxi.payment.application.result.refundRequest;

import java.util.UUID;

public record RefundRequestCreateResult(UUID refundRequestId, String message) {
}
