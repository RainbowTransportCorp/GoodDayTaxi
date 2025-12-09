package com.gooddaytaxi.payment.application.result.refund;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundReadResult(UUID refundId,
                              String status,
                              String reason,
                              String detailReason,
                              UUID requestId,
                              LocalDateTime canceledAt,
                              String transactionKey,
                              String pgFailReason,
                               UUID paymentId,
                               Long amount,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt
                               ) {
}
