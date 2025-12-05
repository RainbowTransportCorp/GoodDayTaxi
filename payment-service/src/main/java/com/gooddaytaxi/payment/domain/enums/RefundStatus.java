package com.gooddaytaxi.payment.domain.enums;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public enum RefundStatus {
    SUCCESS,FAILED;

    public static RefundStatus of(String status) {
        for (RefundStatus rs : RefundStatus.values()) {
            if (rs.name().equalsIgnoreCase(status)) {
                return rs;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_REFUND_STATUS);
    }
}
