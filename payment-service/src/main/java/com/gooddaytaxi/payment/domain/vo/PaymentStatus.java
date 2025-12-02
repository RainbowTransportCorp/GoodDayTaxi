package com.gooddaytaxi.payment.domain.vo;

public enum PaymentStatus {
    PENDING,    // 결제 대기
    IN_PROCESS, // 결제 진행 중
    COMPLETED,  // 결제 완료
    FAILED,     // 결제 실패
    CANCELED,   //결제 취소
    REFUNDED    // 환불
}
