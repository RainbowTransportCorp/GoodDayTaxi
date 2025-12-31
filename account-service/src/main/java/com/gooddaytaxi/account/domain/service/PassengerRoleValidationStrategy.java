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
    public void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor, String slackId) {
        log.debug("승객 역할 검증: slackId present={}", slackId != null);
        
        // 승객은 슬랙 ID 필수
        if (slackId == null || slackId.trim().isEmpty()) {
            throw new com.gooddaytaxi.account.domain.exception.AccountBusinessException(
                com.gooddaytaxi.account.domain.exception.AccountErrorCode.SLACK_ID_REQUIRED);
        }
    }
}
