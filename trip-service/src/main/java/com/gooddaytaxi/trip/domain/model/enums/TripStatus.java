package com.gooddaytaxi.trip.domain.model.enums;

public enum TripStatus {
    READY("준비"),
    STARTED("운행 시작"),
    ENDED("운행 종료"),
    CANCELLED("취소");

    private final String description;

    TripStatus(String description) {

        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public static TripStatus from(String value) {
        for (TripStatus status : TripStatus.values()) {
            // ENUM 이름 비교 (대소문자 무시)
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
            // 한글 설명 비교 (대소문자 무시)
            if (status.getDescription().equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("유효하지 않은 운행 상태입니다: " + value);
    }





}
