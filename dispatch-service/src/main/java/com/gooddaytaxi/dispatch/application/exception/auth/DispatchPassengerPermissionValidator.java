package com.gooddaytaxi.dispatch.application.exception.auth;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import org.springframework.stereotype.Component;

@Component
public class DispatchPassengerPermissionValidator {

    public void validate(UserRole role) {
        // Driver / ADMIN / SYSTEM allowed
        if (role != UserRole.PASSENGER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}