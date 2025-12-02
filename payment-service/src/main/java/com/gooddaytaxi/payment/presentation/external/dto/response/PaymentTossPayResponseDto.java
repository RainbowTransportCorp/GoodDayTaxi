package com.gooddaytaxi.payment.presentation.external.dto.response;

import java.util.UUID;

public record PaymentTossPayResponseDto(UUID paymentId,
                                        Long amount,
                                        String status,
                                        String method) {
}
