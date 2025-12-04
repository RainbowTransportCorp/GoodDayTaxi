package com.gooddaytaxi.payment.presentation.external.mapper.response.payment;

import com.gooddaytaxi.payment.application.result.payment.PaymentUpdateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentUpdateResponseDto;

public class PaymentUpdateResponseMapper {
    public static PaymentUpdateResponseDto toResponse(PaymentUpdateResult result) {
        return new PaymentUpdateResponseDto(
                result.paymentId(),
                result.amount(),
                result.method());
    }
}
