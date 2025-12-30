package com.gooddaytaxi.account.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Account 서비스 전용 도메인 에러 코드
 */
@Getter
@RequiredArgsConstructor
public enum AccountErrorCode {

    // 사용자 관리
    DUPLICATE_EMAIL("ACC001", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT),
    MISSING_VEHICLE_INFO("ACC002", "기사 가입 시 차량 정보가 필수입니다", HttpStatus.BAD_REQUEST),
    DUPLICATE_VEHICLE_NUMBER("ACC003", "이미 등록된 차량번호입니다", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("ACC004", "이메일 또는 비밀번호가 올바르지 않습니다", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("ACC005", "유효하지 않은 리프레시 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("ACC006", "만료된 리프레시 토큰입니다", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("ACC007", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    VEHICLE_REGISTRATION_REQUIRED("ACC012", "차량 등록이 필요합니다", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("ACC013", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    INVALID_INPUT_VALUE("ACC014", "입력 값이 유효하지 않습니다", HttpStatus.BAD_REQUEST),
    SLACK_ID_REQUIRED("ACC015", "슬랙 ID는 필수입니다", HttpStatus.BAD_REQUEST),

    USER_ALREADY_DELETED("ACC016", "이미 삭제된 사용자입니다", HttpStatus.CONFLICT),
    INVALID_USER_STATUS("ACC017", "잘못된 사용자 상태입니다", HttpStatus.BAD_REQUEST),
    DRIVER_PROFILE_NOT_FOUND("ACC018", "기사 프로필을 찾을 수 없습니다", HttpStatus.NOT_FOUND),

    DATABASE_ERROR("ACC019", "데이터베이스 처리 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR("ACC020", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 비밀번호
    WEAK_PASSWORD("ACC021", "비밀번호는 8자 이상, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("ACC022", "비밀번호는 최소 8자 이상이어야 합니다", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_LONG("ACC023", "비밀번호는 50자를 초과할 수 없습니다", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
