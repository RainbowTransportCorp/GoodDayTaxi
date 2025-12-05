package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public record Fare(long value) {
    public Fare {
        if (value < 0) throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
        if(value > 1000000) throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
    }

    public static Fare of(long value) {
        return new Fare(value);
    }
}
