package com.gooddaytaxi.dispatch.application.usecase.reject;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import org.springframework.stereotype.Component;


@Component
public class DispatchRejectPermissionValidator {

    public void validate(UserRole role) {
        if (role != UserRole.DRIVER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}

