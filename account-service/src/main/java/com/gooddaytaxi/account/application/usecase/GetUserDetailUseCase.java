package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.AdminUserDetailResponse;
import com.gooddaytaxi.account.application.mapper.AdminUserDetailMapper;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserDetailUseCase {
    
    private final UserRepository userRepository;
    private final AdminUserDetailMapper adminUserDetailMapper;
    
    public AdminUserDetailResponse execute(String requestUserRole, UUID userId) {
        log.debug("사용자 상세 조회 시작: requestUserRole={}, userId={}", requestUserRole, userId);
        
        validateAdminPermission(requestUserRole);
        User user = findUserById(userId);
        
        log.debug("사용자 상세 조회 완료: userId={}, userName={}", userId, user.getName());
        
        return adminUserDetailMapper.toResponse(user);
    }
    
    private void validateAdminPermission(String requestUserRole) {
        if (!UserRole.ADMIN.name().equals(requestUserRole)) {
            log.warn("ADMIN 권한 없이 사용자 상세 조회 시도: requestRole={}", requestUserRole);
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
}