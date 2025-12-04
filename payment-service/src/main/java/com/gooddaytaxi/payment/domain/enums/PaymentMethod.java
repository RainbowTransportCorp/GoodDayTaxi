package com.gooddaytaxi.payment.domain.enums;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

// 결제 수단 정의
public enum PaymentMethod {
    CASH,       // 현금
    CARD,       // 카드
    TOSS_PAY;   // 토스페이

    public static PaymentMethod of(String method) {
        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.name().equalsIgnoreCase(method)) {
                return paymentMethod;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
    }
}
