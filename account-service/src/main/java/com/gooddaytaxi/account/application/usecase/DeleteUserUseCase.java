package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteUserUseCase {
    
    private final UserRepository userRepository;
    
    public void execute(UUID userUuid) {
        log.debug("사용자 탈퇴 요청 시작: userUuid={}", userUuid);
        
        User user = findUserByUuid(userUuid);
        validateUserCanBeDeleted(user);
        performSoftDelete(user);
        saveUser(user);
        
        log.info("사용자 탈퇴 완료: userUuid={}", userUuid);
    }
    
    private User findUserByUuid(UUID userUuid) {
        return userRepository.findById(userUuid)
                .orElseThrow(() -> new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND));
    }
    
    private void validateUserCanBeDeleted(User user) {
        if (user.isDeleted()) {
            log.warn("이미 삭제된 사용자 탈퇴 시도: userUuid={}", user.getUserUuid());
            throw new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND);
        }
    }
    
    private void performSoftDelete(User user) {
        user.softDelete(user.getUserUuid().toString());
    }
    
    private void saveUser(User user) {
        userRepository.save(user);
    }
}