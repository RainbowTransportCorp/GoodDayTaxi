package com.gooddaytaxi.dispatch.domain.model.enums;

public enum DispatchStatus {
    REQUESTED,   // 승객이 배차를 요청한 초기 상태
    ASSIGNING,   // 드라이버들에게 배차 시도 중
    ASSIGNED,    // 특정 드라이버에게 배차가 할당됨
    ACCEPTED,    // 드라이버가 배차를 최종 수락
    CANCELLED,   // 승객, 기사, 관리자 또는 시스템에 의해 취소됨
    TIMEOUT;      // 전체 배차 프로세스가 타임아웃됨

    public boolean isTerminal() {
        return this == TIMEOUT || this == CANCELLED;
    }

    public boolean isCancelableStatus() {
        return this == DispatchStatus.REQUESTED
                || this == DispatchStatus.ASSIGNING
                || this == DispatchStatus.ASSIGNED;
    }
}

