package com.gooddaytaxi.payment.presentation.external.mapper.response.refund;

import com.gooddaytaxi.payment.application.result.refund.PgRefundResult;
import com.gooddaytaxi.payment.application.result.refund.RefundAdminReadResult;
import com.gooddaytaxi.payment.application.result.refund.RefundReadResult;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.PgRefundResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundAdminReadResponseDto;
import com.gooddaytaxi.payment.presentation.external.dto.response.refund.RefundReadResponseDto;
import org.springframework.data.domain.Page;

public class RefundReadResponseMapper {
    public static RefundReadResponseDto toResponse(RefundReadResult result) {
        return new RefundReadResponseDto(
                result.status(),
                result.amount(),
                result.reason(),
                result.detailReason(),
                result.refundedAt()
        );
    }
    public static Page<RefundReadResponseDto> toPageResponse(Page<RefundReadResult> result) {
        return result.map(RefundReadResponseMapper::toResponse);
    }

    public static Page<RefundAdminReadResponseDto> toPageAdminResponse(Page<RefundAdminReadResult> result) {
        return result.map(RefundReadResponseMapper::toAdminResponse);
    }

    public static RefundAdminReadResponseDto toAdminResponse(RefundAdminReadResult result) {
        return new RefundAdminReadResponseDto(
                result.refundId(),
                result.status(),
                result.reason(),
                result.detailReason(),
                result.requestId(),
                result.refundedAt(),
                result.paymentId(),
                result.amount(),
                pgRefundResponseDto(result.pgRefund()),
                result.createdAt(),
                result.updatedAt()
        );
    }
    public static PgRefundResponseDto pgRefundResponseDto (PgRefundResult pgRefund) {
        return new PgRefundResponseDto(pgRefund.transactionKey(), pgRefund.pgFailReason(), pgRefund.canceledAt());
    }
}
