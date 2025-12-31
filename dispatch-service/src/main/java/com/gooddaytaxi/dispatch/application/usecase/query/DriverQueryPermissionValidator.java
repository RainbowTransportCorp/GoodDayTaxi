package com.gooddaytaxi.dispatch.application.usecase.query;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import org.springframework.stereotype.Component;


@Component
public class DriverQueryPermissionValidator {

    /**
     * 조회 요청자의 역할이 기사(DRIVER)인지 검증한다.
     * 기사가 아닐 경우 DispatchPermissionDeniedException 예외 발생
     *
     * @param role 요청자 역할
     */
    public void validate(UserRole role) {
        if (role != UserRole.DRIVER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}
