package com.gooddaytaxi.payment.presentation.external.mapper.response.payment;

import com.gooddaytaxi.payment.application.result.payment.PaymentCreateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.payment.PaymentCreateResponseDto;

public class PaymentCreateResponseMapper {
    public static PaymentCreateResponseDto toResponse(PaymentCreateResult result) {
        return new PaymentCreateResponseDto(
                result.paymentId(),
                result.message().getMessage());
    }
}
