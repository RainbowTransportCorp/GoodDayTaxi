package com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund;

import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestCreateResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundReqeustCreateResponseDto;

public class RefundRequestCreateResponseMapper {
    public static RefundReqeustCreateResponseDto toResponse(RefundRequestCreateResult result) {
        return new RefundReqeustCreateResponseDto(
                result.refundRequestId(),
                result.message().getMessage()
        );
    }
}
