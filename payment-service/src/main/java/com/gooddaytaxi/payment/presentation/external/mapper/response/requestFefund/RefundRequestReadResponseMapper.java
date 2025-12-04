package com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund;

import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestReadResponseDto;

public class RefundRequestReadResponseMapper {
    public static RefundRequestReadResponseDto toResponse(RefundRequestReadResult result) {
        return new RefundRequestReadResponseDto(
                result.requestId(),
                result.paymentId(),
                result.reason(),
                result.response(),
                result.status());
    }
}
