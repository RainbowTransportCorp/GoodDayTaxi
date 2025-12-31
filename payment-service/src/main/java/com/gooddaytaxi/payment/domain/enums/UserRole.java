package com.gooddaytaxi.payment.domain.enums;

import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;

public enum UserRole {
    PASSENGER, DRIVER, ADMIN, MASTER_ADMIN;

    public static UserRole of(String role) {
        for (UserRole userRole : values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new PaymentException(PaymentErrorCode.INVALID_USER_ROLE);
    }

}
