package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverProfileLookupServiceImpl implements DriverProfileLookupService {
    
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    
    @Override
    public User findDriverByUuid(UUID driverId) {
        log.debug("기사 사용자 조회 실행: driverId={}", driverId);
        
        return userRepository.findByUserUuidAndRoleAndDeletedAtIsNull(driverId, UserRole.DRIVER)
                .orElseThrow(() -> {
                    log.warn("기사 사용자를 찾을 수 없음: driverId={}", driverId);
                    return new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND);
                });
    }
    
    @Override
    public DriverProfile findDriverProfileByUserId(UUID userId) {
        log.debug("기사 프로필 조회 실행: userId={}", userId);
        
        return driverProfileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("기사 프로필을 찾을 수 없음: userId={}", userId);
                    return new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND);
                });
    }
}