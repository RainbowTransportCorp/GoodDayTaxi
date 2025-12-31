package com.gooddaytaxi.payment.domain.enums;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public enum PaymentStatus {
    PENDING,    // 결제 대기
    IN_PROCESS, // 결제 진행 중
    COMPLETED,  // 결제 완료
    FAILED,     // 결제 실패
    CANCELED,   //결제 취소
    REFUNDED;    // 환불

    public static PaymentStatus of(String status) {
        for (PaymentStatus ps : PaymentStatus.values()) {
            if (ps.name().equalsIgnoreCase(status)) {
                return ps;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
    }
}
