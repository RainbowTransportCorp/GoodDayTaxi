package com.gooddaytaxi.payment.application.result.refundRequest;

import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;

import java.util.UUID;

public record RefundRequestReadResult(UUID requestId,
                                      UUID paymentId,
                                      String reason,
                                      String response,
                                      RefundRequestStatus status) {
}
