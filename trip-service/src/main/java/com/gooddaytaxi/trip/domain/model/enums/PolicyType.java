package com.gooddaytaxi.trip.domain.model.enums;

public enum PolicyType {
    STANDARD("일반 표준 요금"),
    PREMIUM("프리미엄 서비스 요금"),
    NIGHT_SURCHARGE("심야 할증 요금");


    private final String description;

    PolicyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PolicyType from(String value) {
        for (PolicyType type : values()) {
            if (type.name().equalsIgnoreCase(value)) { // 대소문자 무시
                return type;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 정책 타입입니다: " + value);
    }



}
