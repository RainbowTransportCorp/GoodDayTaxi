package com.gooddaytaxi.payment.application.event;

import java.util.UUID;

public record RefundSnapshot(
        String reason,           // RefundReason name or code
        String detailReason,     // incidentAt|incidentSummary
        UUID requestId,
        String idempotencyKey
) {}
