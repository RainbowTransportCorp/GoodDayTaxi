package com.gooddaytaxi.payment.application.result.refundRequest;

import com.gooddaytaxi.payment.application.result.payment.PaymentAdminReadResult;
import com.gooddaytaxi.payment.application.result.refund.RefundAdminReadResult;
import com.gooddaytaxi.payment.domain.enums.RefundRequestStatus;

import java.util.UUID;

public record RefundRequestAdminReadResult(
        UUID requestId,
        String reason,
        String response,
        RefundRequestStatus status,
        PaymentAdminReadResult payment,
        RefundAdminReadResult refund
) {
}
