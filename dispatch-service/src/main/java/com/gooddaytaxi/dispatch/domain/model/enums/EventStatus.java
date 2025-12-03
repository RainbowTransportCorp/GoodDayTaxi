package com.gooddaytaxi.dispatch.domain.model.enums;

public enum EventStatus {
    PENDING,   // 이벤트가 생성되었으나 아직 처리되지 않음 (대기 상태)
    SENT,      // 이벤트가 성공적으로 전송됨 (Kafka 등으로 발행 완료)
    FAILED     // 이벤트 전송에 실패함 (재시도 필요)
}
