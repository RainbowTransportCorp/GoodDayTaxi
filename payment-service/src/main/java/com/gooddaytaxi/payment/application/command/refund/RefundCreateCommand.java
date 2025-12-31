package com.gooddaytaxi.payment.application.command.refund;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundCreateCommand(String reason,
                                  String incidentAt,
                                  String incidentSummary,
                                  LocalDateTime executedAt,
                                  UUID requestId) {
}
