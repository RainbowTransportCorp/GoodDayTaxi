package com.gooddaytaxi.payment.presentation.external.mapper.response.requestFefund;

import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestAdminReadResult;
import com.gooddaytaxi.payment.application.result.refundRequest.RefundRequestReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.reqeustRufund.RefundRequestReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.mapper.response.payment.PaymentReadResponseMapper;
import com.gooddaytaxi.payment.presentation.external.mapper.response.refund.RefundReadResponseMapper;
import org.springframework.data.domain.Page;

public class RefundRequestReadResponseMapper {
    public static RefundRequestReadResponseDto toResponse(RefundRequestReadResult result) {
        return new RefundRequestReadResponseDto(
                result.requestId(),
                result.paymentId(),
                result.reason(),
                result.response(),
                result.status().name());
    }

    public static Page<RefundRequestReadResponseDto> toPageResponse(Page<RefundRequestReadResult> results) {
    return results.map(RefundRequestReadResponseMapper::toResponse);
    }

    public static RefundRequestAdminReadResponseDto toAdminResponse(RefundRequestAdminReadResult result) {
        return new RefundRequestAdminReadResponseDto (
                result.requestId(),
                result.reason(),
                result.response(),
                result.status().name(),
                PaymentReadResponseMapper.toAdminResponse(result.payment()),
                RefundReadResponseMapper.toAdminResponse(result.refund())
        );
    }
}
