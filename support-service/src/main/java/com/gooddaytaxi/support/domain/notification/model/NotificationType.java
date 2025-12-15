package com.gooddaytaxi.support.domain.notification.model;

import java.util.Arrays;

/**
 * 알림 타입 - 콜 요청과 배차 수락, 운행 시작과 종료, 결제 완료, 장애 발생에 대한 알림
 */
public enum NotificationType {

    DISPATCH_REQUESTED, // 콜 요청 알림
    DISPATCH_ACCEPTED, // 배차 수락 알림

    TRIP_STARTED,      // 운행 시작 알림
    TRIP_ENDED,        // 운행 종료 알림

    PAYMENT_COMPLETED, // 결제 완료 알림
    REFUND_REQUEST_CREATED, // 환불 요청 알림
    REFUND_REQUEST_REJECTED, // 환불 거절 알림
    REFUND_COMPLETED, // 환불 완료 알림

    ERROR_DETECTED;     // 장애 발생 알림

    public static NotificationType from(String type) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("String이 NotificationType과 일치하지 않습니다: " + type));
    }
}

