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
}
