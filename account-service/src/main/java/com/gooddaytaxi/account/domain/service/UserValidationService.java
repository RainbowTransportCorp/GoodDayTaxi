package com.gooddaytaxi.account.domain.service;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 사용자 도메인 검증 조정 서비스
 * 다양한 검증 서비스들을 조율하는 역할
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final EmailValidator emailValidator;
    private final List<RoleValidationStrategy> roleValidationStrategies;

    /**
     * 회원가입 요청 유효성 검증
     *
     * @param command 회원가입 명령 객체
     * @throws BusinessException 이메일 중복, 차량정보 누락, 차량번호 중복 시 발생
     */
    public void validateSignupRequest(UserSignupCommand command) {
        log.debug("회원가입 검증 시작: email={}, role={}", command.getEmail(), command.getRole());
        
        emailValidator.validateEmailNotDuplicated(command.getEmail());
        validateRoleSpecificInfo(command);
        
        log.debug("회원가입 검증 완료: email={}, role={}", command.getEmail(), command.getRole());
    }

    /**
     * 역할별 특화 정보 검증
     *
     * @param command 회원가입 명령 객체
     * @throws BusinessException 역할별 필수 정보 누락 시 발생
     */
    private void validateRoleSpecificInfo(UserSignupCommand command) {
        roleValidationStrategies.stream()
                .filter(strategy -> strategy.supports(command.getRole()))
                .forEach(strategy -> strategy.validate(
                    command.getRole(),
                    command.getVehicleNumber(),
                    command.getVehicleType(), 
                    command.getVehicleColor(),
                    command.getSlackId()
                ));
    }
}