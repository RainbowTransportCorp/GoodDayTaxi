package com.gooddaytaxi.dispatch.domain.model.enums;

public enum ChangedBy {
    DRIVER,       // 드라이버가 변경함 (수락, 거절 등)
    PASSENGER,    // 승객이 변경함 (배차 취소 등)
    ADMIN,        // 관리자가 변경함 (백오피스 처리)
    SYSTEM        // 시스템이 변경함 (자동취소, 타임아웃 등)
}

