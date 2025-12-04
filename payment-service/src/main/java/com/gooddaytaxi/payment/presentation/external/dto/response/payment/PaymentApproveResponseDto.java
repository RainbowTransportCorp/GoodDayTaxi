package com.gooddaytaxi.payment.presentation.external.dto.response.payment;

import java.util.UUID;

public record PaymentApproveResponseDto(UUID paymentId,
                                        Long amount,
                                        String status,
                                        String method) {
}
