package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 관리자 역할 검증 전략 (일반 관리자 vs 최고 관리자)
 */
@Slf4j
@Component
public class AdminRoleValidationStrategy implements RoleValidationStrategy {
    
    @Override
    public boolean supports(UserRole role) {
        return UserRole.ADMIN.equals(role) || UserRole.MASTER_ADMIN.equals(role);
    }
    
    @Override
    public void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor, String slackId) {
        // 일반 관리자: 차량 정보, 슬랙 ID 불필요
        if (UserRole.ADMIN.equals(userRole)) {
            log.debug("일반 관리자 역할 검증 (슬랙 ID 불필요)");
            return;
        }
        
        // 최고 관리자: 슬랙 ID 필수
        if (UserRole.MASTER_ADMIN.equals(userRole)) {
            log.debug("최고 관리자 역할 검증 (슬랙 ID 필수)");
            if (slackId == null || slackId.trim().isEmpty()) {
                throw new AccountBusinessException(AccountErrorCode.SLACK_ID_REQUIRED);
            }
        }
    }
}