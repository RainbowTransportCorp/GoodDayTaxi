package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.DeleteUserCommand;
import com.gooddaytaxi.account.application.dto.DeleteUserResponse;
import com.gooddaytaxi.account.application.mapper.DeleteUserMapper;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
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
public class AdminDeleteUserUseCase {
    
    private final UserRepository userRepository;
    private final DeleteUserMapper deleteUserMapper;
    
    public DeleteUserResponse execute(String requestUserRole, UUID userId, DeleteUserCommand command) {
        log.debug("관리자 사용자 삭제 시작: requestUserRole={}, userId={}, reason={}", 
                requestUserRole, userId, command.getReason());
        
        validateAdminPermission(requestUserRole);
        User user = findUserById(userId);
        validateUserNotAlreadyDeleted(user);
        performSoftDelete(user, command.getReason());
        saveUser(user);
        
        log.debug("관리자 사용자 삭제 완료: userId={}, deletedAt={}", userId, user.getDeletedAt());
        
        return deleteUserMapper.toResponse(user);
    }
    
    private void validateAdminPermission(String requestUserRole) {
        if (!UserRole.ADMIN.name().equals(requestUserRole)) {
            log.warn("ADMIN 권한 없이 사용자 삭제 시도: requestRole={}", requestUserRole);
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
    
    private void validateUserNotAlreadyDeleted(User user) {
        if (user.isDeleted()) {
            log.warn("이미 삭제된 사용자 삭제 시도: userId={}", user.getUserUuid());
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
    }
    
    private void performSoftDelete(User user, String reason) {
        user.softDelete(reason);
    }
    
    private void saveUser(User user) {
        userRepository.save(user);
    }
}