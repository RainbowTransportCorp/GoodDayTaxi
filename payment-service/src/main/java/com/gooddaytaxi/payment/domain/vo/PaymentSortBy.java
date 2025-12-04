package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public enum PaymentSortBy {
    method,
    status,
    createdAt,
    updatedAt;

    public static void checkValid(String sortBy) {
        for (PaymentSortBy orderBy : PaymentSortBy.values()) {
            if (orderBy.name().equals(sortBy)) {
                return;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_SEARCH_PERIOD);
    }
}
