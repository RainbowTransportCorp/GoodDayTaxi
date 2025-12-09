package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public enum RefundSortBy {
    method, status, reason, canceledAt;

    public static void checkValid(String value) {
        for (RefundSortBy sortBy : RefundSortBy.values()) {
            if (sortBy.name().equalsIgnoreCase(value)) {
                return;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_SORT_BY);
    }
}
