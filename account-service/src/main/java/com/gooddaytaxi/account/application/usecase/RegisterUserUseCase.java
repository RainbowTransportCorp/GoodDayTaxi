package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.UserSignupCommand;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.repository.UserWriteRepository;
import com.gooddaytaxi.account.domain.service.PasswordEncoder;
import com.gooddaytaxi.account.domain.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 회원가입 처리 유스케이스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterUserUseCase {

    private final UserWriteRepository userWriteRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidationService userValidationService;

    /**
     * 사용자 회원가입 처리
     *
     * @param command 회원가입 명령 객체 (이메일, 비밀번호, 이름, 전화번호, 역할, 차량정보 등)
     * @return 생성된 사용자 ID
     * @throws BusinessException 이메일 중복, 차량정보 누락, 차량번호 중복 시 발생
     */
    public String execute(UserSignupCommand command) {
        userValidationService.validateSignupRequest(command);

        // 사용자 생성
        User user = User.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .name(command.getName())
                .phoneNumber(command.getPhoneNumber())
                .role(command.getRole())
                .build();

        User savedUser = userWriteRepository.save(user);

        // 기사인 경우 DriverProfile 정보 저장
        if (command.getRole() == UserRole.DRIVER) {
            createDriverProfile(savedUser, command);
        }

        return savedUser.getUserId();
    }

    /**
     * 기사 프로필 생성 및 저장
     *
     * @param user 생성된 사용자 엔티티
     * @param command 회원가입 명령 객체 (차량정보 포함)
     */
    private void createDriverProfile(User user, UserSignupCommand command) {
        DriverProfile driverProfile = DriverProfile.builder()
                .user(user)
                .vehicleNumber(command.getVehicleNumber())
                .vehicleType(command.getVehicleType())
                .vehicleColor(command.getVehicleColor())
                .build();

        driverProfileRepository.save(driverProfile);
    }
}