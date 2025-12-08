package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.UpdateDriverProfileCommand;
import com.gooddaytaxi.account.application.dto.UpdateDriverProfileResponse;
import com.gooddaytaxi.account.application.mapper.UpdateDriverProfileMapper;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.service.DriverProfileLookupService;
import com.gooddaytaxi.account.domain.service.DriverProfileUpdateService;
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
public class UpdateDriverProfileUseCase {
    
    private final DriverProfileLookupService driverProfileLookupService;
    private final DriverProfileUpdateService driverProfileUpdateService;
    private final DriverProfileRepository driverProfileRepository;
    private final UpdateDriverProfileMapper updateDriverProfileMapper;
    
    public UpdateDriverProfileResponse execute(UUID requestUserUuid, UUID targetDriverId, UpdateDriverProfileCommand command) {
        log.debug("기사 프로필 수정 시작: requestUser={}, targetDriver={}", requestUserUuid, targetDriverId);
        
        validateDriverOwnership(requestUserUuid, targetDriverId);
        
        User driver = findDriver(targetDriverId);
        DriverProfile driverProfile = findDriverProfile(targetDriverId);
        
        updateDriverProfile(driverProfile, command);
        saveDriverProfile(driverProfile);
        
        UpdateDriverProfileResponse response = buildResponse(targetDriverId);
        
        log.info("기사 프로필 수정 완료: driverId={}, vehicleNumber={}", 
                targetDriverId, command.getVehicleNumber());
        
        return response;
    }
    
    private void validateDriverOwnership(UUID requestUserUuid, UUID targetDriverId) {
        if (!requestUserUuid.equals(targetDriverId)) {
            log.warn("본인 프로필이 아닌 기사 프로필 수정 시도: requestUser={}, target={}", 
                    requestUserUuid, targetDriverId);
            throw new AccountBusinessException(AccountErrorCode.ACCESS_DENIED);
        }
    }
    
    private User findDriver(UUID driverId) {
        return driverProfileLookupService.findDriverByUuid(driverId);
    }
    
    private DriverProfile findDriverProfile(UUID driverId) {
        return driverProfileLookupService.findDriverProfileByUserId(driverId);
    }
    
    private void updateDriverProfile(DriverProfile driverProfile, UpdateDriverProfileCommand command) {
        driverProfileUpdateService.updateVehicleInfo(
                driverProfile, 
                command.getVehicleNumber(), 
                command.getVehicleType(), 
                command.getVehicleColor()
        );
    }
    
    private void saveDriverProfile(DriverProfile driverProfile) {
        driverProfileRepository.save(driverProfile);
    }
    
    private UpdateDriverProfileResponse buildResponse(UUID driverId) {
        return updateDriverProfileMapper.toResponse(driverId);
    }
}