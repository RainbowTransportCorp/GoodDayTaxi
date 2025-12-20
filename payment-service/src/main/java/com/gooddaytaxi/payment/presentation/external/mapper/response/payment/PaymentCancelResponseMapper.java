package com.gooddaytaxi.payment.presentation.external.mapper.response.payment;

import com.gooddaytaxi.payment.application.result.payment.PaymentCancelResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentCancelResponseDto;

public class PaymentCancelResponseMapper {
    public static PaymentCancelResponseDto toResponse(PaymentCancelResult result) {
        return new PaymentCancelResponseDto(
                result.id(),
                result.message().getMessage()
        );
    }
}
