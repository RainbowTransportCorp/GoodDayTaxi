package com.gooddaytaxi.payment.presentation.external.mapper.response.refund;

import com.gooddaytaxi.payment.application.result.refund.RefundAdminReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundAdminReadResponseDto;
import org.springframework.data.domain.Page;

public class RefundReadResponseMapper {
    public static RefundAdminReadResponseDto toResponse(RefundAdminReadResult result) {
        return new RefundAdminReadResponseDto(
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

    public static Page<RefundAdminReadResponseDto> toPageResponse(Page<RefundAdminReadResult> result) {
        return result.map(RefundReadResponseMapper::toResponse);
    }

    public static RefundAdminReadResponseDto toAdminResponse(RefundAdminReadResult result) {
        return new RefundAdminReadResponseDto(
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
