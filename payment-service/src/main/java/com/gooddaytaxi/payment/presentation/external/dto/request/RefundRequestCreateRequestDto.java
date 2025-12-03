package com.gooddaytaxi.payment.presentation.external.dto.request;

import java.util.UUID;

public record RefundRequestCreateRequestDto(UUID paymentId,
                                            String reason) {
}
