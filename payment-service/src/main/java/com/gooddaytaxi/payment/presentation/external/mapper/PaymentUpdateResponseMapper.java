package com.gooddaytaxi.payment.presentation.external.mapper;

import com.gooddaytaxi.payment.application.result.PaymentUpdateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentUpdateResponseDto;

public class PaymentUpdateResponseMapper {
    public static PaymentUpdateResponseDto toResponse(PaymentUpdateResult result) {
        return new PaymentUpdateResponseDto(
                result.paymentId(),
                result.amount(),
                result.method());
    }
}
