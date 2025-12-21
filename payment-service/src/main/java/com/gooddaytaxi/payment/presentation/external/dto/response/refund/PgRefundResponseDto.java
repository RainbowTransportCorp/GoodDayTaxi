package com.gooddaytaxi.payment.presentation.external.dto.response.refund;

import java.time.LocalDateTime;

public record PgRefundResponseDto(String transactionKey,
                                  String pgFailReason,
                                  LocalDateTime canceledAt) {
}
