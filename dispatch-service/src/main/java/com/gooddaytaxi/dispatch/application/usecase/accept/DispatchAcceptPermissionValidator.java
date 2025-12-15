package com.gooddaytaxi.dispatch.application.usecase.accept;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.domain.exception.DispatchNotAssignedDriverException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DispatchAcceptPermissionValidator {

    public void validate(UserRole role) {
        if (role != UserRole.DRIVER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}
