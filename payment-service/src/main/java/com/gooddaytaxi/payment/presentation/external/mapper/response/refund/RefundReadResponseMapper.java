package com.gooddaytaxi.payment.presentation.external.mapper.response.refund;

import com.gooddaytaxi.payment.application.result.refund.RefundReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundReadResponseDto;

public class RefundReadResponseMapper {
    public static RefundReadResponseDto toResponse(RefundReadResult result) {
        return new RefundReadResponseDto(
                result.refundId(),
                result.status(),
                result.reason(),
                result.detailReason(),
                result.requestId(),
                result.canceledAt(),
                result.transactionKey(),
                result.pgFailReason(),
                result.paymentId(),
                result.amount(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
