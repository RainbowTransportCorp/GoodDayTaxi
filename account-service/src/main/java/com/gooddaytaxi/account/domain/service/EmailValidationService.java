package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserReadRepository;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 이메일 검증 전용 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailValidationService implements EmailValidator {
    
    private final UserReadRepository userReadRepository;
    
    /**
     * 이메일 중복 검증 (활성 사용자 중에서)
     *
     * @param email 검증할 이메일 주소
     * @throws BusinessException 활성 상태의 이메일이 이미 존재할 때 발생
     */
    public void validateEmailNotDuplicated(String email) {
        log.debug("이메일 중복 검증 시작: email={}", email);
        
        if (userReadRepository.existsByEmailAndStatus(email, UserStatus.ACTIVE)) {
            log.warn("이메일 중복: email={}", email);
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        log.debug("이메일 중복 검증 통과: email={}", email);
    }
}
