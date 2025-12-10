package com.gooddaytaxi.dispatch.application.exception;

import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
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
            case DRIVER -> "passenger 권한이 필요합니다.";
            case PASSENGER -> "driver 권한이 필요합니다.";
            default -> "권한이 없습니다.";
        };
    }
}


