package com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund;

import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCancelResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCancelResponseDto;

public class RefundRequestCancelResponseMapper {
    public static RefundReqeustCancelResponseDto toResponse(RefundRequestCancelResult result) {
        return new RefundReqeustCancelResponseDto(
                result.requestId(),
                result.message()
        );
    }
}
