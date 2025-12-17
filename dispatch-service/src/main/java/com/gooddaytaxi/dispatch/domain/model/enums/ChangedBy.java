package com.gooddaytaxi.dispatch.domain.model.enums;

/**
 * history 기록 시 배차 상태 변경을 한 주체
 */
public enum ChangedBy {
    DRIVER,       // 드라이버가 변경함 (수락, 거절 등)
    PASSENGER,    // 승객이 변경함 (배차 취소 등)
    MASTER_ADMIN, // 최고 관리자가 변경함
    SYSTEM        // 시스템이 변경함 (자동취소, 타임아웃 등)
}

