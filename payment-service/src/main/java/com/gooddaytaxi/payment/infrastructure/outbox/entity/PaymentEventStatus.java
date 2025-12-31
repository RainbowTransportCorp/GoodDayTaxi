package com.gooddaytaxi.payment.infrastructure.outbox.entity;

public enum PaymentEventStatus {
    PENDING,   // 이벤트가 생성되었으나 아직 처리되지 않음 (대기 상태)
    SENT,      // 이벤트가 성공적으로 전송됨 (Kafka 등으로 발행 완료)
    FAILED    //세번 시도후에 안되면 짐시 격리
}
