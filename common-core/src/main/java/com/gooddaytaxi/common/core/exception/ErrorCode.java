package com.gooddaytaxi.common.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 전체에서 사용하는 에러 코드 정의 (Web 기술 제거 버전)
 *
 * Common 모듈은 Web(HTTP) 기술에 의존하면 안 되므로,
 * HttpStatus 대신 도메인 내부용 ErrorLevel로 대체합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common 에러
    INVALID_INPUT_VALUE(ErrorLevel.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(ErrorLevel.BAD_REQUEST, "C002", "잘못된 타입의 값입니다."),
    MISSING_REQUEST_PARAMETER(ErrorLevel.BAD_REQUEST, "C003", "필수 요청 파라미터가 누락되었습니다."),
    METHOD_NOT_ALLOWED(ErrorLevel.METHOD_NOT_ALLOWED, "C004", "지원하지 않는 HTTP 메소드입니다."),
    ACCESS_DENIED(ErrorLevel.FORBIDDEN, "C005", "접근이 거부되었습니다."),
    INTERNAL_SERVER_ERROR(ErrorLevel.INTERNAL_SERVER_ERROR, "C006", "서버 내부 오류가 발생했습니다."),
    INVALID_USER_ROLE(ErrorLevel.BAD_REQUEST, "C007", "잘못된 권한명입니다."),

    // User
    USER_NOT_FOUND(ErrorLevel.NOT_FOUND, "USR001", "사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(ErrorLevel.UNAUTHORIZED, "USR002", "인증이 필요합니다."),
    DUPLICATED_USER(ErrorLevel.CONFLICT, "USR003", "이미 존재하는 사용자입니다."),
    INVALID_STATUS_CHANGE(ErrorLevel.BAD_REQUEST, "USR004", "잘못된 상태 변경 요청입니다"),
    INVALID_ROLE_CHANGE(ErrorLevel.BAD_REQUEST, "USR005", "잘못된 역할 변경 요청입니다"),
    ALREADY_DELETED_USER(ErrorLevel.BAD_REQUEST, "USR006", "이미 삭제된 사용자입니다."),
    INVALID_CREDENTIALS(ErrorLevel.UNAUTHORIZED, "USR007", "아이디 또는 비밀번호가 잘못되었습니다."),
    SERVICE_UNAVAILABLE(ErrorLevel.SERVICE_UNAVAILABLE, "USR008", "현재 서비스 이용이 불가합니다."),
    REQUEST_TIMEOUT(ErrorLevel.TIMEOUT, "USR009", "요청 시간이 초과되었습니다.");

    private final ErrorLevel level;
    private final String code;
    private final String message;
}
