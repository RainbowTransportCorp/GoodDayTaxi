package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 승객 역할 검증 전략 (추가 검증 없음)
 */
@Slf4j
@Component
public class PassengerRoleValidationStrategy implements RoleValidationStrategy {
    
    @Override
    public boolean supports(UserRole role) {
        return UserRole.PASSENGER.equals(role);
    }
    
    @Override
    public void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor) {
        log.debug("승객 역할 검증 (추가 검증 없음)");
        // PASSENGER는 추가 검증 없음
    }
}
