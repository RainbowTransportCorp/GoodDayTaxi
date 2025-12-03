package com.gooddaytaxi.dispatch.domain.model.enums;

public enum AssignmentStatus {
    SENT,        // 특정 드라이버에게 배차 요청이 전송됨
    ACCEPTED,    // 드라이버가 배차 요청을 수락함
    REJECTED,    // 드라이버가 배차 요청을 거절함
    TIMEOUT      // 드라이버가 응답하지 않아 시간 초과됨
}
