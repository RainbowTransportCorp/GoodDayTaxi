package com.gooddaytaxi.dispatch.application.exception;

import com.gooddaytaxi.dispatch.application.validator.UserRole;
import lombok.Getter;

@Getter
public class DispatchPermissionDeniedException extends RuntimeException {

    private final String code = "DISPATCH_PERMISSION_DENIED";
    private final UserRole role;

    public DispatchPermissionDeniedException(UserRole role) {
        super(makeMessage(role));
        this.role = role;
    }

    private static String makeMessage(UserRole role) {
        return switch (role) {
            case DRIVER -> "드라이버는 호출을 생성할 수 없습니다.";
            default -> "호출 생성 권한이 없습니다.";
        };
    }
}


