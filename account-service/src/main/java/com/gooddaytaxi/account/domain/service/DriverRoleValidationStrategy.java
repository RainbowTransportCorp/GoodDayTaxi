package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 기사 역할 검증 전략
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DriverRoleValidationStrategy implements RoleValidationStrategy {
    
    private final DriverValidator driverValidator;
    
    @Override
    public boolean supports(UserRole role) {
        return UserRole.DRIVER.equals(role);
    }
    
    @Override
    public void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor) {
        log.debug("기사 역할 검증 실행: vehicleNumber={}", vehicleNumber);
        driverValidator.validateDriverInfo(vehicleNumber, vehicleType, vehicleColor);
    }
}
