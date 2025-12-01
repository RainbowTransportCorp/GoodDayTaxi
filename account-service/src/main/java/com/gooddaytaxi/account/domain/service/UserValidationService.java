package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.repository.UserReadRepository;
import com.gooddaytaxi.common.core.exception.BusinessException;
import com.gooddaytaxi.common.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 사용자 도메인 검증 서비스
 */
@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserReadRepository userReadRepository;
    private final DriverProfileRepository driverProfileRepository;

    /**
     * 회원가입 요청 유효성 검증
     *
     * @param command 회원가입 명령 객체
     * @throws BusinessException 이메일 중복, 차량정보 누락, 차량번호 중복 시 발생
     */
    public void validateSignupRequest(UserSignupCommand command) {
        validateEmailNotDuplicated(command.getEmail());
        validateRoleSpecificInfo(command);
    }

    /**
     * 역할별 특화 정보 검증
     *
     * @param command 회원가입 명령 객체
     * @throws BusinessException 역할별 필수 정보 누락 시 발생
     */
    private void validateRoleSpecificInfo(UserSignupCommand command) {
        if (command.getRole() == UserRole.DRIVER) {
            validateDriverInfo(command);
        }
        // PASSENGER와 ADMIN은 차량 정보 불필요
    }

    /**
     * 이메일 중복 검증 (활성 사용자 중에서)
     *
     * @param email 검증할 이메일 주소
     * @throws BusinessException 활성 상태의 이메일이 이미 존재할 때 발생
     */
    public void validateEmailNotDuplicated(String email) {
        if (userReadRepository.existsByEmailAndStatus(email, UserStatus.ACTIVE)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    /**
     * 기사 정보 검증 (차량 정보 필수 + 차량번호 중복 체크)
     *
     * @param command 회원가입 명령 객체 (차량정보 포함)
     * @throws BusinessException 차량정보 누락 또는 차량번호 중복 시 발생
     */
    private void validateDriverInfo(UserSignupCommand command) {
        validateVehicleInfoRequired(command);
        validateVehicleNumberNotDuplicated(command.getVehicleNumber());
    }

    /**
     * 차량 정보 필수값 검증
     *
     * @param command 회원가입 명령 객체
     * @throws BusinessException 차량 정보가 누락된 경우 발생
     */
    private void validateVehicleInfoRequired(UserSignupCommand command) {
        if (isVehicleInfoMissing(command)) {
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