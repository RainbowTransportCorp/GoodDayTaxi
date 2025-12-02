package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.UpdateUserProfileCommand;
import com.gooddaytaxi.account.application.dto.UpdateUserProfileResponse;
import com.gooddaytaxi.account.application.mapper.UpdateUserProfileMapper;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import com.gooddaytaxi.account.domain.service.UserProfileUpdateService;
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
@Transactional
public class UpdateUserProfileUseCase {
    
    private final UserRepository userRepository;
    private final UserProfileUpdateService userProfileUpdateService;
    private final UpdateUserProfileMapper updateUserProfileMapper;
    
    public UpdateUserProfileResponse execute(UUID userUuid, UpdateUserProfileCommand command) {
        log.debug("사용자 정보 수정 시작: userUuid={}", userUuid);
        
        User user = findUserByUuid(userUuid);
        updateUserProfile(user, command);
        saveUser(user);
        
        UpdateUserProfileResponse response = updateUserProfileMapper.toResponse(userUuid);
        
        log.info("사용자 정보 수정 완료: userUuid={}, name={}", userUuid, command.getName());
        
        return response;
    }
    
    private User findUserByUuid(UUID userUuid) {
        return userRepository.findById(userUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
    
    private void updateUserProfile(User user, UpdateUserProfileCommand command) {
        userProfileUpdateService.updateProfile(user, command.getName(), command.getPhoneNumber());
    }
    
    private void saveUser(User user) {
        userRepository.save(user);
    }
}