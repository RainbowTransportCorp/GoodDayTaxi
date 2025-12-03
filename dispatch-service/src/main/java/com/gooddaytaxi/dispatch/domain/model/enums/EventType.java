package com.gooddaytaxi.dispatch.domain.model.enums;

public enum EventType {
    DISPATCH_CREATED,              // 배차가 생성됨
    DISPATCH_STATUS_CHANGED,       // 배차 상태가 변경됨 (from → to)
    DISPATCH_ASSIGNED,             // 드라이버에게 배차가 할당됨
    DISPATCH_ACCEPTED,             // 드라이버가 배차를 수락함
    DISPATCH_REJECTED,             // 드라이버가 배차를 거절함
    DISPATCH_CANCELLED,            // 배차가 취소됨
    DISPATCH_TIMEOUT,              // 배차 전체가 타임아웃됨
    DISPATCH_TRIP_CREATE_REQUEST   // Trip 서비스로 운행 생성 요청
}
