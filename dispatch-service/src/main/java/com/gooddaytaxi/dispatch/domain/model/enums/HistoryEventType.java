package com.gooddaytaxi.dispatch.domain.model.enums;

public enum HistoryEventType {
    STATUS_CHANGED,   // 배차 상태가 변경됨 (from → to)
    USER_CREATED,     // 승객이 배차를 생성함
    AUTO_CANCELED,     // 시스템에 의해 자동 취소됨 (타임아웃, 정책 등)
    USER_CANCELED,     // 승객이 배차를 취소함
    DRIVER_REJECTED,   // 드라이버가 배차를 거절함
    REASSIGN_REQUESTED,
    TIMEOUT          // 응답 지연 등으로 배차가 타임아웃됨
}

