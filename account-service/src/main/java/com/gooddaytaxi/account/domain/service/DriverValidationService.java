package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 기사 정보 검증 전용 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DriverValidationService implements DriverValidator {
    
    private final DriverProfileRepository driverProfileRepository;
    
    /**
     * 기사 정보 검증 (차량 정보 필수 + 차량번호 중복 체크)
     *
     * @param command 회원가입 명령 객체 (차량정보 포함)
     * @throws BusinessException 차량정보 누락 또는 차량번호 중복 시 발생
     */
    public void validateDriverInfo(UserSignupCommand command) {
        log.debug("기사 정보 검증 시작: vehicleNumber={}", command.getVehicleNumber());
        
        validateVehicleInfoRequired(command);
        validateVehicleNumberNotDuplicated(command.getVehicleNumber());
        
        log.debug("기사 정보 검증 통과: vehicleNumber={}", command.getVehicleNumber());
    }
    
    /**
     * 차량 정보 필수값 검증
     *
     * @param command 회원가입 명령 객체
     * @throws BusinessException 차량 정보가 누락된 경우 발생
     */
    private void validateVehicleInfoRequired(UserSignupCommand command) {
        if (isVehicleInfoMissing(command)) {
            log.warn("차량 정보 누락: vehicleNumber={}, vehicleType={}, vehicleColor={}", 
                    command.getVehicleNumber(), command.getVehicleType(), command.getVehicleColor());
            throw new BusinessException(ErrorCode.MISSING_VEHICLE_INFO);
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
            throw new BusinessException(ErrorCode.DUPLICATE_VEHICLE_NUMBER);
        }
    }
    
    /**
     * 차량 정보 누락 여부 확인
     *
     * @param command 회원가입 명령 객체
     * @return 차량 정보가 누락된 경우 true, 모두 있으면 false
     */
    private boolean isVehicleInfoMissing(UserSignupCommand command) {
        return command.getVehicleNumber() == null || command.getVehicleNumber().trim().isEmpty() ||
               command.getVehicleType() == null || command.getVehicleType().trim().isEmpty() ||
               command.getVehicleColor() == null || command.getVehicleColor().trim().isEmpty();
    }
}
