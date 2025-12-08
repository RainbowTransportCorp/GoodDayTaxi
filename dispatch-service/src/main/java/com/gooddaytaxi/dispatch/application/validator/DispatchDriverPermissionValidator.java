package com.gooddaytaxi.dispatch.application.validator;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import org.springframework.stereotype.Component;

@Component
public class DispatchDriverPermissionValidator {

    public void validate(UserRole role) {
        // PASSENGER / ADMIN / SYSTEM allowed
        if (role != UserRole.DRIVER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}