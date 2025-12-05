package com.gooddaytaxi.account.domain.exception;

import com.gooddaytaxi.common.core.exception.ErrorLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Account 서비스 전용 도메인 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    
    // 기존 common-core에서 이관된 사용자 관리 에러들
    DUPLICATE_EMAIL(ErrorLevel.CONFLICT, "ACC001", "이미 사용 중인 이메일입니다"),
    MISSING_VEHICLE_INFO(ErrorLevel.BAD_REQUEST, "ACC002", "기사 가입 시 차량 정보가 필수입니다"),
    DUPLICATE_VEHICLE_NUMBER(ErrorLevel.CONFLICT, "ACC003", "이미 등록된 차량번호입니다"),
    INVALID_CREDENTIALS(ErrorLevel.UNAUTHORIZED, "ACC004", "이메일 또는 비밀번호가 올바르지 않습니다"),
    INVALID_REFRESH_TOKEN(ErrorLevel.UNAUTHORIZED, "ACC005", "유효하지 않은 리프레시 토큰입니다"),
    EXPIRED_REFRESH_TOKEN(ErrorLevel.UNAUTHORIZED, "ACC006", "만료된 리프레시 토큰입니다"),
    USER_NOT_FOUND(ErrorLevel.NOT_FOUND, "ACC007", "사용자를 찾을 수 없습니다"),
    
    // Account 서비스만의 고유 에러들
    ACCOUNT_LOCKED(ErrorLevel.FORBIDDEN, "ACC008", "계정이 잠겨있습니다"),
    ACCOUNT_SUSPENDED(ErrorLevel.FORBIDDEN, "ACC009", "계정이 정지된 상태입니다"),
    PROFILE_UPDATE_RESTRICTED(ErrorLevel.FORBIDDEN, "ACC010", "프로필 수정 권한이 없습니다"),
    DRIVER_LICENSE_EXPIRED(ErrorLevel.BAD_REQUEST, "ACC011", "운전면허가 만료되었습니다"),
    VEHICLE_REGISTRATION_REQUIRED(ErrorLevel.BAD_REQUEST, "ACC012", "차량 등록이 필요합니다");
    
    private final ErrorLevel level;
    private final String code;
    private final String message;
}