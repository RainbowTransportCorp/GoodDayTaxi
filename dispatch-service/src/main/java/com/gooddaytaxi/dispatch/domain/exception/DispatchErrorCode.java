package com.gooddaytaxi.dispatch.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DispatchErrorCode {

    INVALID_STATE(
            "DISPATCH_INVALID_STATE",
            "현재 상태에서는 해당 동작을 수행할 수 없습니다.",
            400
    ),

    CANNOT_CANCEL(
            "DISPATCH_CANNOT_CANCEL",
            "이미 종료된 배차는 취소할 수 없습니다.",
            400
    ),

    DISPATCH_NOT_FOUND(
        "DISPATCH_NOT_FOUND",
        "배차 정보를 찾을 수 없습니다.",
        404
    ),

    NOT_ASSIGNED_DRIVER(
            "DISPATCH_NOT_ASSIGNED_DRIVER",
            "해당 기사에게 배정된 콜이 아닙니다.",
            403
    ),

    NOT_OWNER_PASSENGER(
            "DISPATCH_NOT_OWNER_PASSENGER",
            "해당 승객이 생성한 콜이 아닙니다.",
            403
    ),

    DRIVER_UNAVAILABLE(
            "DRIVER_UNAVAILABLE",
            "현재 배정 가능한 기사가 없습니다.",
            409
    ),

    ALREADY_ACCEPTED(
            "DISPATCH_ALREADY_ACCEPTED",
            "이미 다른 기사에 의해 수락된 배차입니다.",
            409
    ),

    CANNOT_ASSIGN(
            "CANNOT_ASSIGN_DRIVER",
            "해당 기사에게 배정할 수 없습니다.",
            400
    );

    private final String code;     // 에러 코드 (프론트/로그용)
    private final String message;  // 도메인 규칙 위반 메시지
    private final int status;      // HTTP Status (숫자)
}
