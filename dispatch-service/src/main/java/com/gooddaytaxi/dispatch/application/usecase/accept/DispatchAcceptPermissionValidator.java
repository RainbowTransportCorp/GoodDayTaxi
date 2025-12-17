package com.gooddaytaxi.dispatch.application.usecase.accept;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import org.springframework.stereotype.Component;

@Component
public class DispatchAcceptPermissionValidator {

    /**
     * 배차 수락 요청자의 권한을 검증
     * 권한이 Driver가 아닐 경우 DispatchPermissionDeniedException 예외 발생
     * @param role 요청자의 권한
     */
    public void validate(UserRole role) {
        if (role != UserRole.DRIVER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}
