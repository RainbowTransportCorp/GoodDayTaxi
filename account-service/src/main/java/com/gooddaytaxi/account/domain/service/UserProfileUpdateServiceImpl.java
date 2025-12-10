package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserProfileUpdateServiceImpl implements UserProfileUpdateService {
    
    @Override
    public void updateProfile(User user, String name, String phoneNumber) {
        log.debug("사용자 프로필 업데이트 실행: userUuid={}", user.getUserUuid());
        
        validateUpdateData(name, phoneNumber);
        user.updateProfile(name, phoneNumber);
        
        log.debug("사용자 프로필 업데이트 완료: userUuid={}", user.getUserUuid());
    }
    
    private void validateUpdateData(String name, String phoneNumber) {
        if (name == null || name.trim().isEmpty()) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
    }
}