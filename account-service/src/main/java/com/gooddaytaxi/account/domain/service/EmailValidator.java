package com.gooddaytaxi.account.domain.service;

/**
 * 이메일 검증 인터페이스
 */
public interface EmailValidator {
    
    /**
     * 이메일 중복 검증
     *
     * @param email 검증할 이메일 주소
     * @throws BusinessException 중복된 이메일이 있을 경우
     */
    void validateEmailNotDuplicated(String email);
}
