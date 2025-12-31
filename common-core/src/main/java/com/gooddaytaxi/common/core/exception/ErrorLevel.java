package com.gooddaytaxi.common.core.exception;

/**
 * 시스템 전역 수준에서 사용하는 "에러 심각도 / HTTP 매핑 레벨" 정의
 *
 * ⚠ 주의:
 *  - HttpStatus를 직접 참조하지 않는다.
 *  - 실제 HTTP 코드 매핑은 각 서비스의 GlobalExceptionHandler가 담당한다.
 *  - 여기서는 "에러 카테고리"만 의미적으로 분류한다.
 */
public enum ErrorLevel {

    /**
     * 잘못된 요청, 사용자 입력 오류 등 (400 계열)
     */
    BAD_REQUEST,

    /**
     * 인증 실패 (401)
     */
    UNAUTHORIZED,

    /**
     * 접근 권한 부족 (403)
     */
    FORBIDDEN,

    /**
     * 자원 없음 (404)
     */
    NOT_FOUND,

    /**
     * 요청된 메서드 허용되지 않음 (405)
     */
    METHOD_NOT_ALLOWED,

    /**
     * 비즈니스 충돌 / 자료 상태 불일치 (409)
     */
    CONFLICT,

    /**
     * 외부 API 연동 실패 (502 Bad Gateway)
     */
    BAD_GATEWAY,

    /**
     * 서버 내부 문제 (500)
     */
    INTERNAL_SERVER_ERROR,

    /**
     * 외부 시스템/인프라 장애 (503)
     */
    SERVICE_UNAVAILABLE,

    /**
     * 응답 지연/타임아웃 (408 / 504)
     */
    TIMEOUT
}
