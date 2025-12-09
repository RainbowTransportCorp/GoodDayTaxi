package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 관리자 역할 검증 전략 (슬랙 ID 불필요)
 */
@Slf4j
@Component
public class AdminRoleValidationStrategy implements RoleValidationStrategy {
    
    @Override
    public boolean supports(UserRole role) {
        return UserRole.ADMIN.equals(role);
    }
    
    @Override
    public void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor, String slackId) {
        log.debug("관리자 역할 검증 (슬랙 ID 불필요)");
        // 관리자는 슬랙 ID가 필요하지 않으므로 별도 검증 없음
    }
}