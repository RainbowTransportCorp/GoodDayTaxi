package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.UserRole;

/**
 * 역할별 검증 전략 인터페이스
 */
public interface RoleValidationStrategy {
    
    /**
     * 이 전략이 지원하는 역할인지 확인
     *
     * @param role 사용자 역할
     * @return 지원하면 true, 아니면 false
     */
    boolean supports(UserRole role);
    
    /**
     * 역할별 검증 수행
     *
     * @param userRole 사용자 역할
     * @param vehicleNumber 차량번호 (기사인 경우 필수)
     * @param vehicleType 차량종류 (기사인 경우 필수)
     * @param vehicleColor 차량색상 (기사인 경우 필수)
     * @param slackId 슬랙 ID (승객, 기사인 경우 필수)
     * @throws BusinessException 검증 실패 시
     */
    void validate(UserRole userRole, String vehicleNumber, String vehicleType, String vehicleColor, String slackId);
}
