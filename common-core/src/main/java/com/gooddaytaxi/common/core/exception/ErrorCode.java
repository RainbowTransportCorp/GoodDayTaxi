package com.gooddaytaxi.common.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 전체에서 사용하는 공통 에러 코드 정의
 *
 * <p>📌 이 Enum은 "공통 모듈(common-core)"에 존재하므로 다음 원칙을 반드시 지킨다.
 *
 * 1. HTTP, Spring Web 기술에 의존하지 않는다.
 *    - HttpStatus, ResponseEntity 등을 여기서 직접 사용하지 않는다.
 *    - HTTP 상태값 매핑은 각 서비스의 GlobalExceptionHandler에서 담당한다.
 *
 * 2. JPA / DB / 개별 도메인에 의존하지 않는다.
 *    - Entity, Repository, @Table, @Column 등 DB 관련 개념을 끌고 오지 않는다.
 *    - "Dispatch 전용", "Payment 전용" 같은 도메인별 상태는 여기에 넣지 않는다.
 *
 * 3. "시스템 전역에서 통일되어야 하는 에러 코드"만 정의한다.
 *    - 모든 서비스가 공통으로 이해하는 범용 에러들만 둔다.
 *    - 특정 서비스에서만 쓰이는 비즈니스 에러는 각 서비스 내부 Enum으로 분리한다.
 *
 * 4. 이 Enum은 "API 응답의 code / message"에만 관여한다.
 *    - level → 각 서비스의 GlobalExceptionHandler에서 HTTP 상태로 변환
 *    - code  → 클라이언트/로그에서 참조하는 식별자 (예: "C001")
 *    - message → 기본 에러 메시지(한국어 기준, 필요 시 Multi-Language Layer에서 가공)
 *
 * ✅ 개념적으로 이 파일은 "공통 에러 사전(카탈로그)"라고 생각하면 된다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =========================================================
    // 🔹 Common / 시스템 공통 에러
    // =========================================================

    /**
     * 잘못된 입력값 (Validation, Body, Query, Path 등)
     * - 예: 필수 값 누락, 형식 위반, 길이 제약 위반 등
     */
    INVALID_INPUT_VALUE(ErrorLevel.BAD_REQUEST, "C001", "잘못된 입력값입니다."),

    /**
     * 타입 변환 오류
     * - 예: 숫자여야 하는데 문자열이 들어온 경우
     */
    INVALID_TYPE_VALUE(ErrorLevel.BAD_REQUEST, "C002", "잘못된 타입의 값입니다."),

    /**
     * 필수 요청 파라미터 누락
     * - 예: query string / header / path variable 누락
     */
    MISSING_REQUEST_PARAMETER(ErrorLevel.BAD_REQUEST, "C003", "필수 요청 파라미터가 누락되었습니다."),

    /**
     * 지원하지 않는 HTTP Method
     * - 예: GET만 허용된 API에 POST로 호출
     *
     * ⚠ 여기서는 "HTTP 405"를 직접 쓰지 않는다.
     *   - ErrorLevel.METHOD_NOT_ALLOWED → 각 서비스 예외 핸들러에서 405로 변환
     */
    METHOD_NOT_ALLOWED(ErrorLevel.METHOD_NOT_ALLOWED, "C004", "지원하지 않는 HTTP 메소드입니다."),

    /**
     * 인증은 되었지만, 권한이 없는 경우
     * - 예: PASSENGER가 DRIVER 전용 API 호출
     */
    ACCESS_DENIED(ErrorLevel.FORBIDDEN, "C005", "접근이 거부되었습니다."),

    /**
     * 처리되지 않은 서버 내부 에러의 기본 코드
     * - 예상하지 못한 예외가 올라온 경우 fallback 용도로 사용
     */
    INTERNAL_SERVER_ERROR(ErrorLevel.INTERNAL_SERVER_ERROR, "C006", "서버 내부 오류가 발생했습니다."),

    /**
     * 시스템 전역에서 사용하는 공통 비즈니스 제약 위반
     * - 예: 잘못된 Enum 값, 지원하지 않는 상태 전이 등
     */
    INVALID_STATE(ErrorLevel.BAD_REQUEST, "C007", "지원하지 않는 상태입니다."),


    // =========================================================
    // 🔹 인증 / 인가 관련 (Auth / User 공통)
    //   - "User 서비스만의 도메인 에러"가 아니라,
    //     전체 시스템 어디서나 의미가 통하는 에러만 정의
    // =========================================================

    /**
     * Access Token이 없거나, 형식이 잘못된 경우
     */
    AUTH_TOKEN_MISSING(ErrorLevel.UNAUTHORIZED, "A001", "인증 토큰이 존재하지 않거나 올바르지 않습니다."),

    /**
     * 만료된 JWT 토큰
     */
    AUTH_TOKEN_EXPIRED(ErrorLevel.UNAUTHORIZED, "A002", "만료된 토큰입니다."),

    /**
     * 토큰은 유효하지만, 토큰에 담긴 권한/역할이 요청 자원에 맞지 않는 경우
     */
    AUTH_FORBIDDEN_ROLE(ErrorLevel.FORBIDDEN, "A003", "해당 리소스에 접근할 권한이 없습니다."),


    // =========================================================
    // 🔹 외부 시스템 / 인프라 공통 에러
    //   - DB, Redis, 메시지 브로커 등 "공통 인프라"에 대한 에러 코드
    //   - 특정 서비스의 도메인 로직이 아니라, 시스템 인프라 차원의 문제들
    // =========================================================

    /**
     * 공통 DB 연결 불가, 타임아웃 등
     * - 실제 예외 타입은 각 서비스에서 DB Exception을 핸들링하면서 이 코드로 변환
     */
    INFRA_DATABASE_ERROR(ErrorLevel.SERVICE_UNAVAILABLE, "I001", "데이터베이스 처리 중 오류가 발생했습니다."),

    /**
     * Redis / 캐시 서버와 통신 실패
     */
    INFRA_CACHE_UNAVAILABLE(ErrorLevel.SERVICE_UNAVAILABLE, "I002", "캐시 서버와 통신할 수 없습니다."),

    /**
     * Kafka / 메시지 브로커와 통신 실패
     */
    INFRA_MESSAGE_BROKER_ERROR(ErrorLevel.SERVICE_UNAVAILABLE, "I003", "메시지 브로커 처리 중 오류가 발생했습니다."),

    /**
     * 외부 HTTP API 연동 실패
     * - 예: PG사, 외부 지도 API 등
     */
    EXTERNAL_API_ERROR(ErrorLevel.BAD_GATEWAY, "I004", "외부 API 호출 중 오류가 발생했습니다.");


    // =========================================================
    // 🔹 도메인별 에러 코드는 각 서비스에서 관리
    //   - account-service: AccountErrorCode
    //   - dispatch-service: DispatchErrorCode  
    //   - payment-service: PaymentErrorCode
    //   - trip-service: TripErrorCode 등
    // =========================================================

    // =========================================================
    // 필드 정의
    // =========================================================

    /**
     * 에러 심각도 & HTTP 매핑 레벨
     *
     * - BAD_REQUEST           → 보통 400
     * - UNAUTHORIZED          → 401
     * - FORBIDDEN             → 403
     * - NOT_FOUND             → 404
     * - METHOD_NOT_ALLOWED    → 405
     * - CONFLICT              → 409
     * - SERVICE_UNAVAILABLE   → 503
     * - INTERNAL_SERVER_ERROR → 500
     *
     * ⚠ 주의:
     *  - 여기서 HttpStatus를 직접 쓰지 않고,
     *    ErrorLevel → HttpStatus 매핑은 각 서비스의 GlobalExceptionHandler에서 담당한다.
     */
    private final ErrorLevel level;

    /**
     * 에러 식별 코드
     *
     * - 형식 예시:
     *   - C001 ~ C099 : Common / 시스템 공통 에러
     *   - A001 ~ A099 : Auth / 인증·인가 관련
     *   - I001 ~ I099 : Infra / 외부 시스템, 인프라 관련
     *
     * - 이 값은 API 응답, 로그, 모니터링, 대시보드 등에서
     *   "에러 유형"을 구분하기 위한 키로 사용된다.
     *
     * - 개별 도메인 서비스(Dispatch, Trip, Payment 등)에서만 의미 있는
     *   비즈니스 에러 코드는 여기에 추가하지 않는다.
     *   → 그런 코드는 각 서비스 전용 ErrorCode(or DomainErrorCode) Enum으로 관리한다.
     */
    private final String code;

    /**
     * 기본 에러 메시지 (한국어)
     *
     * - 클라이언트/프론트에 바로 노출해도 되는 수준의 메시지로 작성한다.
     * - 나중에 다국어(i18n)를 도입할 경우:
     *   - 이 필드를 그대로 쓰지 않고,
     *   - code 값만 사용해서 MessageSource에서 언어별로 조회할 수 있다.
     */
    private final String message;
}
