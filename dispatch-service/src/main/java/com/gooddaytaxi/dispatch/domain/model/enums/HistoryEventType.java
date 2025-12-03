package com.gooddaytaxi.dispatch.domain.model.enums;

public enum HistoryEventType {
    STATUS_CHANGE,   // 배차 상태가 변경됨 (from → to)
    AUTO_CANCEL,     // 시스템에 의해 자동 취소됨 (타임아웃, 정책 등)
    USER_CANCEL,     // 승객이 배차를 취소함
    DRIVER_REJECT,   // 드라이버가 배차를 거절함
    TIMEOUT          // 응답 지연 등으로 배차가 타임아웃됨
}

