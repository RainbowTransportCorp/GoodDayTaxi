package com.gooddaytaxi.trip.application.exception;
import org.springframework.http.HttpStatus;

public enum TripErrorCode {

    // ===== Role / Permission =====
    PASSENGER_ROLE_REQUIRED("TRIP_401", HttpStatus.FORBIDDEN, "승객 권한이 필요합니다."),
    DRIVER_ROLE_REQUIRED("TRIP_402", HttpStatus.FORBIDDEN, "기사 권한이 필요합니다."),
    ADMIN_ROLE_REQUIRED("TRIP_403", HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다."),
    MASTER_ADMIN_ROLE_REQUIRED("TRIP_404", HttpStatus.FORBIDDEN, "마스터 관리자 권한이 필요합니다."),
    ADMIN_OR_MASTER_REQUIRED("TRIP_405", HttpStatus.FORBIDDEN, "관리자 또는 마스터 관리자 권한이 필요합니다."),
    TRIP_PERMISSION_DENIED("TRIP_406", HttpStatus.FORBIDDEN, "해당 운행에 대한 권한이 없습니다."),

    // ===== Status / Transition =====
    TRIP_STATUS_INVALID("TRIP_409", HttpStatus.CONFLICT, "운행 상태가 올바르지 않습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    TripErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
