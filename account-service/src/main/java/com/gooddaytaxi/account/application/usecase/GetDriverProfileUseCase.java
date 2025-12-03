package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.DriverProfileResponse;
import com.gooddaytaxi.account.application.mapper.DriverProfileMapper;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.service.DriverProfileLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDriverProfileUseCase {
    
    private final DriverProfileLookupService driverProfileLookupService;
    private final DriverProfileMapper driverProfileMapper;
    
    public DriverProfileResponse execute(UUID driverId) {
        log.debug("기사 프로필 조회 시작: driverId={}", driverId);
        
        User driver = findDriverByUuid(driverId);
        DriverProfile driverProfile = findDriverProfileByUserId(driverId);
        
        DriverProfileResponse response = buildDriverProfileResponse(driver, driverProfile);
        
        log.debug("기사 프로필 조회 완료: driverId={}, name={}", driverId, driver.getName());
        
        return response;
    }
    
    private User findDriverByUuid(UUID driverId) {
        return driverProfileLookupService.findDriverByUuid(driverId);
    }
    
    private DriverProfile findDriverProfileByUserId(UUID userId) {
        return driverProfileLookupService.findDriverProfileByUserId(userId);
    }
    
    private DriverProfileResponse buildDriverProfileResponse(User driver, DriverProfile driverProfile) {
        return driverProfileMapper.toResponse(driver, driverProfile);
    }
}