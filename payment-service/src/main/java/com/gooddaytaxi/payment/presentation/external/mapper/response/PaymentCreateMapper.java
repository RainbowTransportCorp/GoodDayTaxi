package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.PaymentCreateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.PaymentCreateResponseDto;

public class PaymentCreateMapper {
    public static PaymentCreateResponseDto toResponse(PaymentCreateResult result) {
        return new PaymentCreateResponseDto(
                result.paymentId(),
                result.method(),
                result.amount());
    }
}
