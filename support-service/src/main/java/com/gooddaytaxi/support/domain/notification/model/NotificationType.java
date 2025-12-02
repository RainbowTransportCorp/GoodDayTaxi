package com.gooddaytaxi.support.domain.notification.model;

import java.util.UUID;

/**
 * 알림 타입 - 배차 할당과 수락, 운행 시작과 종료, 결제 완료, 장애 발생에 대한 알림
 */
public enum NotificationType {

    DISPATCH_ASSIGNED, // 배차 할당 알림
    DISPATCH_ACCEPTED, // 배차 수락 알림

    TRIP_STARTED,      // 운행 시작 알림
    TRIP_ENDED,        // 운행 종료 알림

    PAYMENT_COMPLETED, // 결제 완료 알림

    ERROR_DETECTED     // 장애 발생 알림
}

