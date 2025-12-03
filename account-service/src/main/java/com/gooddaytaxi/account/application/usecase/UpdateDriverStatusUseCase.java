package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.UpdateDriverStatusCommand;
import com.gooddaytaxi.account.application.dto.UpdateDriverStatusResponse;
import com.gooddaytaxi.account.application.mapper.UpdateDriverStatusMapper;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.service.DriverProfileLookupService;
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
public class UpdateDriverStatusUseCase {
    
    private final DriverProfileLookupService driverProfileLookupService;
    private final DriverProfileRepository driverProfileRepository;
    private final UpdateDriverStatusMapper updateDriverStatusMapper;
    
    public UpdateDriverStatusResponse execute(UUID requestUserUuid, UUID driverUuid, UpdateDriverStatusCommand command) {
        log.debug("기사 상태 변경 시작: requestUser={}, driver={}, status={}", 
                requestUserUuid, driverUuid, command.getOnlineStatus());
        
        validateOwnership(requestUserUuid, driverUuid);
        DriverProfile driverProfile = findDriverProfile(driverUuid);
        updateDriverStatus(driverProfile, command.getOnlineStatus());
        saveDriverProfile(driverProfile);
        
        log.debug("기사 상태 변경 완료: driver={}, newStatus={}", 
                driverUuid, driverProfile.getOnlineStatus());
        
        return updateDriverStatusMapper.toResponse(driverProfile);
    }
    
    private void validateOwnership(UUID requestUserUuid, UUID driverUuid) {
        if (!requestUserUuid.equals(driverUuid)) {
            log.warn("본인이 아닌 기사 프로필 상태 변경 시도: requestUser={}, targetDriver={}", 
                    requestUserUuid, driverUuid);
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
    
    private DriverProfile findDriverProfile(UUID driverUuid) {
        return driverProfileLookupService.findDriverProfileByUserId(driverUuid);
    }
    
    private void updateDriverStatus(DriverProfile driverProfile, String newStatus) {
        driverProfile.changeOnlineStatus(newStatus);
    }
    
    private void saveDriverProfile(DriverProfile driverProfile) {
        driverProfileRepository.save(driverProfile);
    }
}