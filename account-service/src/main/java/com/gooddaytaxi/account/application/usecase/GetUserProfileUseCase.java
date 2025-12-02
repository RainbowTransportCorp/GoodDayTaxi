package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.UserProfileResponse;
import com.gooddaytaxi.account.application.mapper.UserProfileMapper;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserProfileUseCase {
    
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    
    public UserProfileResponse execute(UUID userUuid) {
        log.debug("사용자 프로필 조회 시작: userUuid={}", userUuid);
        
        User user = findUserByUuid(userUuid);
        UserProfileResponse response = userProfileMapper.toResponse(user);
        
        log.debug("사용자 프로필 조회 완료: userUuid={}, email={}", userUuid, user.getEmail());
        
        return response;
    }
    
    private User findUserByUuid(UUID userUuid) {
        return userRepository.findById(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}