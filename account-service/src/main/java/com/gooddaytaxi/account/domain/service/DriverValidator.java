package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;

/**
 * 기사 정보 검증 인터페이스
 */
public interface DriverValidator {
    
    /**
     * 기사 정보 검증
     *
     * @param command 회원가입 명령 객체 (차량정보 포함)
     * @throws BusinessException 차량정보 누락 또는 차량번호 중복 시 발생
     */
    void validateDriverInfo(UserSignupCommand command);
}
