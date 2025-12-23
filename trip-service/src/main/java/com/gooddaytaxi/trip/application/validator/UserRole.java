package com.gooddaytaxi.trip.application.validator;

import com.gooddaytaxi.trip.domain.exception.TripDomainException;

public enum UserRole {
    PASSENGER,    // 승객
    DRIVER,       // 기사
    ADMIN,        // 일반 관리자 (조회만)
    MASTER_ADMIN;  // 최고 관리자 (모든 권한)
    public static UserRole of(String role) {
        for (UserRole userRole : values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new TripDomainException("Invalid user role: " + role);
    }
}
