package com.gooddaytaxi.payment.application.result.refund;

import java.time.LocalDateTime;

public record RefundReadResult(String status,
                               Long amount,
                               String reason,
                               String detailReason,
                               LocalDateTime refundedAt
                               ) {
}
