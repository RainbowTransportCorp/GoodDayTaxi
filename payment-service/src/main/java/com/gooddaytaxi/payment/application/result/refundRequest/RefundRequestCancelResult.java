package com.gooddaytaxi.payment.application.result.refundRequest;

import java.util.UUID;

public record RefundRequestCancelResult(UUID requestId, String message) {
}
