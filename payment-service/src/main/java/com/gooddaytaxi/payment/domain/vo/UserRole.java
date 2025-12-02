package com.gooddaytaxi.payment.domain.vo;

import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;

public enum UserRole {
    PASSENGER, DRIVER, ADMIN;

    public static UserRole of(String role) {
        for (UserRole userRole : values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

}
