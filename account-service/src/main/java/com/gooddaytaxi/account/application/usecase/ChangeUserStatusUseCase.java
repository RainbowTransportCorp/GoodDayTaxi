package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.ChangeUserStatusCommand;
import com.gooddaytaxi.account.application.dto.ChangeUserStatusResponse;
import com.gooddaytaxi.account.application.mapper.ChangeUserStatusMapper;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChangeUserStatusUseCase {
    
    private final UserRepository userRepository;
    private final ChangeUserStatusMapper changeUserStatusMapper;
    
    public ChangeUserStatusResponse execute(String requestUserRole, UUID userId, ChangeUserStatusCommand command) {
        log.debug("사용자 상태 변경 시작: requestUserRole={}, userId={}, newStatus={}", 
                requestUserRole, userId, command.getStatus());
        
        validateAdminPermission(requestUserRole);
        User user = findUserById(userId);
        UserStatus newStatus = parseUserStatus(command.getStatus());
        changeUserStatus(user, newStatus);
        saveUser(user);
        
        log.debug("사용자 상태 변경 완료: userId={}, newStatus={}", userId, newStatus);
        
        return changeUserStatusMapper.toResponse(user);
    }
    
    private void validateAdminPermission(String requestUserRole) {
        if (!UserRole.ADMIN.name().equals(requestUserRole)) {
            log.warn("ADMIN 권한 없이 사용자 상태 변경 시도: requestRole={}", requestUserRole);
            throw new AccountBusinessException(AccountErrorCode.ACCESS_DENIED);
        }
    }
    
    private User findUserById(UUID userId) {
        return userRepository.findByUserUuid(userId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: userId={}", userId);
                    return new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND);
                });
    }
    
    private UserStatus parseUserStatus(String status) {
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 사용자 상태: status={}", status);
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
    }
    
    private void changeUserStatus(User user, UserStatus newStatus) {
        user.changeStatus(newStatus);
    }
    
    private void saveUser(User user) {
        userRepository.save(user);
    }
}