package com.gooddaytaxi.payment.presentation.external.mapper.response;

import com.gooddaytaxi.payment.application.result.RefundRequestCreateResult;
import com.gooddaytaxi.payment.presentation.external.dto.request.RefundReqeustCreateResponseDto;

public class RefundRequestCreateResponseMapper {
    public static RefundReqeustCreateResponseDto toResponse(RefundRequestCreateResult result) {
        return new RefundReqeustCreateResponseDto(
                result.refundRequestId(),
                result.message()
        );
    }
}
