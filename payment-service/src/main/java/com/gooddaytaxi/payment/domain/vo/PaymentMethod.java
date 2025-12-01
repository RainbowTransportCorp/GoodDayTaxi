package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;

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
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
}
