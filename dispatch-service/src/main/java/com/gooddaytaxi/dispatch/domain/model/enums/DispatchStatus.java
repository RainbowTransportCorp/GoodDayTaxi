package com.gooddaytaxi.dispatch.domain.model.enums;

public enum DispatchStatus {
    REQUESTED,   // 승객이 배차를 요청한 초기 상태
    ASSIGNING,   // 드라이버들에게 배차 시도 중
    ASSIGNED,    // 특정 드라이버에게 배차가 할당됨
    ACCEPTED,    // 드라이버가 배차를 최종 수락
    CANCELED,   // 승객, 기사, 관리자 또는 시스템에 의해 취소됨
    TIMEOUT,      // 전체 배차 프로세스가 타임아웃됨
    TRIP_REQUEST, // Trip 서비스에 운행 생성 요청을 보낸 상태 (응답 대기)
    TRIP_READY;  // Trip 서비스에서 운행 대기중이라는 응답을 받은 상태

    public boolean isReassignable() {
        return this == ASSIGNING || this == ASSIGNED;
    }

    public boolean isTerminal() {
        return this == TIMEOUT || this == CANCELED;
    }

    public boolean canTimeout() {
        return this == REQUESTED
            || this == ASSIGNING
            || this == ASSIGNED;
    }

    public boolean canForceTimeout() {
        return this == REQUESTED
            || this == ASSIGNING
            || this == ASSIGNED
            || this == ACCEPTED
            || this == TRIP_REQUEST;
    }

    /**
     * TripReady 이벤트를 기다리는 상태인지 여부 확인
     */
    public boolean isWaitingTripReady() {
        return this == TRIP_REQUEST;
    }

    /**
     * 이미 운행이 대기중인지 확인
     */
    public boolean isTripReady() {
        return this == TRIP_READY;
    }
}

