package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.PaymentCancelResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentCancelResponseDto;

public class PaymentCancelResponseMapper {
    public static PaymentCancelResponseDto toResponse(PaymentCancelResult result) {
        return new PaymentCancelResponseDto(
                result.id(),
                result.status()
        );
    }
}
