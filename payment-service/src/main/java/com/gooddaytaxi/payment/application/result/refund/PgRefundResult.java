package com.gooddaytaxi.payment.application.result.refund;

import java.time.LocalDateTime;

public record PgRefundResult(
        String transactionKey,
        String pgFailReason,
        LocalDateTime canceledAt
        ) {
}
