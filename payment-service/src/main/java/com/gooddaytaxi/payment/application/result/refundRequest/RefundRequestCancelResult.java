package com.gooddaytaxi.payment.application.result.refundRequest;

import com.gooddaytaxi.payment.application.message.SuccessMessage;

import java.util.UUID;

public record RefundRequestCancelResult(UUID requestId, SuccessMessage message) {
}
