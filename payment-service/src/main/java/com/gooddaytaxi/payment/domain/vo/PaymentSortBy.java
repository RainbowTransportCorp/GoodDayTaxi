package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;

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
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
}
