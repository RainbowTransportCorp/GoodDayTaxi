package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverProfileUpdateServiceImpl implements DriverProfileUpdateService {
    
    @Override
    public void updateVehicleInfo(DriverProfile driverProfile, String vehicleNumber, String vehicleType, String vehicleColor) {
        log.debug("기사 차량 정보 업데이트 실행: profileId={}", driverProfile.getUserId());
        
        validateDriverProfileUpdate(vehicleNumber, vehicleType, vehicleColor);
        driverProfile.updateVehicleInfo(vehicleNumber, vehicleType, vehicleColor);
        
        log.debug("기사 차량 정보 업데이트 완료: profileId={}, vehicleNumber={}", 
                driverProfile.getUserId(), vehicleNumber);
    }
    
    @Override
    public void validateDriverProfileUpdate(String vehicleNumber, String vehicleType, String vehicleColor) {
        log.debug("기사 프로필 업데이트 검증 실행");
        
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
        if (vehicleColor == null || vehicleColor.trim().isEmpty()) {
            throw new AccountBusinessException(AccountErrorCode.INVALID_INPUT_VALUE);
        }
        
        log.debug("기사 프로필 업데이트 검증 완료");
    }
}