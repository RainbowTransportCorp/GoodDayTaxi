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
    public void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor, String slackId) {
        log.debug("기사 역할 검증 실행: vehicleNumber={}, slackId present={}", vehicleNumber, slackId != null);
        
        // 차량 정보 검증
        driverValidator.validateDriverInfo(vehicleNumber, vehicleType, vehicleColor);
        
        // 기사는 슬랙 ID 필수
        if (slackId == null || slackId.trim().isEmpty()) {
            throw new com.gooddaytaxi.account.domain.exception.AccountBusinessException(
                com.gooddaytaxi.account.domain.exception.AccountErrorCode.SLACK_ID_REQUIRED);
        }
    }
}
