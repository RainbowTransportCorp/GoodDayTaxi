package com.gooddaytaxi.support.application.dto;


import lombok.Getter;

@Getter
public enum RefundReason {
    CUSTOMER_REQUEST("고객 요청 승인"),
    COMPANY_FAULT_SYSTEM("시스템/앱 장애로 인한 보상"),
    DUPLICATE_PAYMENT("중복 결제 처리"),
    PROMOTION_COMPENSATION("이벤트/보상성 환불"),
    ADMIN_ADJUSTMENT("관리자 임의 조정"),
    ETC("기타 사유");

    private final String description;

    RefundReason(String description) {
        this.description = description;
    }

    public static RefundReason of(String reason) {
        for (RefundReason rr : RefundReason.values()) {
            if (rr.name().equalsIgnoreCase(reason)) {
                return rr;
            }
        }
//        throw new PaymentException(PaymentErrorCode.INVALID_REFUND_REASON);
        throw new IllegalArgumentException("환불 사유 종류 아님");
    }
}

