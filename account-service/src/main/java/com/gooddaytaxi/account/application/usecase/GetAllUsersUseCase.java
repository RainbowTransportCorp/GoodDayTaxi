package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.AdminUserListResponse;
import com.gooddaytaxi.account.application.mapper.AdminUserListMapper;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllUsersUseCase {
    
    private final UserRepository userRepository;
    private final AdminUserListMapper adminUserListMapper;
    
    public List<AdminUserListResponse> execute(String requestUserRole) {
        log.debug("전체 사용자 조회 시작: requestUserRole={}", requestUserRole);
        
        validateAdminPermission(requestUserRole);
        List<User> allUsers = findAllUsers();
        
        log.debug("전체 사용자 조회 완료: userCount={}", allUsers.size());
        
        return adminUserListMapper.toResponseList(allUsers);
    }
    
    private void validateAdminPermission(String requestUserRole) {
        if (!UserRole.ADMIN.name().equals(requestUserRole)) {
            log.warn("ADMIN 권한 없이 전체 사용자 조회 시도: requestRole={}", requestUserRole);
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
    
    private List<User> findAllUsers() {
        return userRepository.findAll();
    }
}