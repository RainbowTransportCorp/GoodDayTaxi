package com.gooddaytaxi.payment.presentation.external.mapper.response.refund;

import com.gooddaytaxi.payment.application.result.refund.RefundCreateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundCreateResponseDto;

public class RefundCreateResponseMapper {
    public static RefundCreateResponseDto toResponse(RefundCreateResult result) {
        return new RefundCreateResponseDto(result.paymentId(), result.message().getMessage());
    }
}
