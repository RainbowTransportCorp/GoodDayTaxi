package com.gooddaytaxi.support.application.policy;

/**
 * 사용자 역할 타입 - 승객/기사/일반관리자/최고관리자 구분
 */
public enum UserRole {
    PASSENGER,    // 승객
    DRIVER,       // 기사
    ADMIN,        // 일반 관리자 (조회만)
    MASTER_ADMIN  // 최고 관리자 (모든 권한)
}