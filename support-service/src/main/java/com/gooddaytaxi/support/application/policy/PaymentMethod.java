package com.gooddaytaxi.support.application.policy;

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
        throw new IllegalArgumentException("결제 수단 종류 아님");
    }
}
