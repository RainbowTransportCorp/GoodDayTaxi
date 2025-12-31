package com.gooddaytaxi.dispatch.application.usecase.create;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import org.springframework.stereotype.Component;

@Component
public class DispatchCreatePermissionValidator {

    public void validate(UserRole role) {
        //승객, 마스터만 가능
        if (role == UserRole.DRIVER || role == UserRole.ADMIN || role == UserRole.SYSTEM) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}