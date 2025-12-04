package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public enum RefundRequestSortBy {
    method,
    status,
    createdAt,
    updatedAt;

    public static void checkValid(String sortBy) {
        for (RefundRequestSortBy orderBy : RefundRequestSortBy.values()) {
            if (orderBy.name().equals(sortBy)) {
                return;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_SEARCH_PERIOD);
    }
}
