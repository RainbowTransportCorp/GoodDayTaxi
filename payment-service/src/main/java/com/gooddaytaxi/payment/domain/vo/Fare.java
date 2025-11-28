package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;

public record Fare(long value) {
    public Fare {
        if (value < 0) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if(value > 1000000) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    public static Fare of(long value) {
        return new Fare(value);
    }
}
