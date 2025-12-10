package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 비밀번호 검증 서비스
 */
@Slf4j
@Service
public class PasswordValidationService {
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;
    
    // 영문 대문자, 소문자, 숫자, 특수문자를 포함한 8자 이상의 비밀번호
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    /**
     * 비밀번호 유효성 검증
     * 
     * @param password 검증할 비밀번호
     * @throws AccountBusinessException 비밀번호가 요구사항을 만족하지 않을 경우
     */
    public void validatePassword(String password) {
        log.debug("비밀번호 검증 시작");
        
        if (password == null || password.trim().isEmpty()) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
        
        validateLength(password);
        validateStrength(password);
        
        log.debug("비밀번호 검증 완료");
    }
    
    /**
     * 비밀번호 길이 검증
     */
    private void validateLength(String password) {
        if (password.length() < MIN_LENGTH) {
            log.warn("비밀번호 길이 부족: actualLength={}, minLength={}", password.length(), MIN_LENGTH);
            throw new AccountBusinessException(AccountErrorCode.PASSWORD_TOO_SHORT);
        }
        
        if (password.length() > MAX_LENGTH) {
            log.warn("비밀번호 길이 초과: actualLength={}, maxLength={}", password.length(), MAX_LENGTH);
            throw new AccountBusinessException(AccountErrorCode.PASSWORD_TOO_LONG);
        }
    }
    
    /**
     * 비밀번호 강도 검증
     */
    private void validateStrength(String password) {
        if (!STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            log.warn("비밀번호 강도 부족");
            throw new AccountBusinessException(AccountErrorCode.WEAK_PASSWORD);
        }
    }
}