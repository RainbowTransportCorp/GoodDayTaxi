package com.gooddaytaxi.dispatch.application.usecase.query;

import com.gooddaytaxi.dispatch.application.exception.DispatchPermissionDeniedException;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import org.springframework.stereotype.Component;


@Component
public class PassengerQueryPermissionValidator {

    public void validate(UserRole role) {
        if (role != UserRole.PASSENGER) {
            throw new DispatchPermissionDeniedException(role);
        }
    }
}
