package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 기사 정보 검증 전용 서비스
 * 예시: AccountErrorCode도 사용 가능
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DriverValidationService implements DriverValidator {
    
    private final DriverProfileRepository driverProfileRepository;
    
    /**
     * 기사 정보 검증 (차량 정보 필수 + 차량번호 중복 체크)
     *
     * @param vehicleNumber 차량번호
     * @param vehicleType 차량종류
     * @param vehicleColor 차량색상
     * @throws BusinessException 차량정보 누락 또는 차량번호 중복 시 발생
     */
    @Override
    public void validateDriverInfo(String vehicleNumber, String vehicleType, String vehicleColor) {
        log.debug("기사 정보 검증 시작: vehicleNumber={}", vehicleNumber);
        
        validateVehicleInfoRequired(vehicleNumber, vehicleType, vehicleColor);
        validateVehicleNumberNotDuplicated(vehicleNumber);
        
        log.debug("기사 정보 검증 통과: vehicleNumber={}", vehicleNumber);
    }
    
    /**
     * 차량 정보 필수값 검증
     *
     * @param vehicleNumber 차량번호
     * @param vehicleType 차량종류
     * @param vehicleColor 차량색상
     * @throws BusinessException 차량 정보가 누락된 경우 발생
     */
    private void validateVehicleInfoRequired(String vehicleNumber, String vehicleType, String vehicleColor) {
        if (isVehicleInfoMissing(vehicleNumber, vehicleType, vehicleColor)) {
            log.warn("차량 정보 누락: vehicleNumber={}, vehicleType={}, vehicleColor={}", 
                    vehicleNumber, vehicleType, vehicleColor);
            throw new AccountBusinessException(AccountErrorCode.MISSING_VEHICLE_INFO);
        }
    }
    
    /**
     * 차량번호 중복 검증
     *
     * @param vehicleNumber 검증할 차량번호
     * @throws BusinessException 차량번호가 이미 존재할 때 발생
     */
    private void validateVehicleNumberNotDuplicated(String vehicleNumber) {
        if (driverProfileRepository.existsByVehicleNumber(vehicleNumber)) {
            log.warn("차량번호 중복: vehicleNumber={}", vehicleNumber);
            throw new AccountBusinessException(AccountErrorCode.DUPLICATE_VEHICLE_NUMBER);
        }
    }
    
    /**
     * 차량 등록 필수 검증 (Account 도메인 특화 에러 사용 예시)
     * 
     * @param hasVehicleRegistration 차량 등록 여부
     */
    public void validateVehicleRegistration(boolean hasVehicleRegistration) {
        if (!hasVehicleRegistration) {
            log.warn("차량 등록이 필요한 기사 계정");
            throw new AccountBusinessException(AccountErrorCode.VEHICLE_REGISTRATION_REQUIRED);
        }
    }

    /**
     * 차량 정보 누락 여부 확인
     *
     * @param vehicleNumber 차량번호
     * @param vehicleType 차량종류
     * @param vehicleColor 차량색상
     * @return 차량 정보가 누락된 경우 true, 모두 있으면 false
     */
    private boolean isVehicleInfoMissing(String vehicleNumber, String vehicleType, String vehicleColor) {
        return vehicleNumber == null || vehicleNumber.trim().isEmpty() ||
               vehicleType == null || vehicleType.trim().isEmpty() ||
               vehicleColor == null || vehicleColor.trim().isEmpty();
    }
}
