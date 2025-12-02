package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;
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
     * @param command 회원가입 명령 객체
     * @throws BusinessException 검증 실패 시
     */
    void validate(UserSignupCommand command);
}
