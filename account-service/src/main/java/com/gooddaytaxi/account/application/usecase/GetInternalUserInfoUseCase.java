package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.InternalUserInfoResponse;
import com.gooddaytaxi.account.application.mapper.InternalUserInfoMapper;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Internal API용 사용자 정보 조회 UseCase
 * 
 * Single Responsibility: Internal API 전용 사용자 정보 조회 로직만 담당
 * Open/Closed: 새로운 Internal API 요구사항이 생기면 새 UseCase를 추가
 * Dependency Inversion: Repository와 Mapper 인터페이스에 의존
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetInternalUserInfoUseCase {
    
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final InternalUserInfoMapper mapper;
    
    /**
     * 사용자 ID로 Internal API용 정보 조회
     * 
     * @param userId 조회할 사용자 ID
     * @return Internal API 응답용 사용자 정보
     * @throws AccountBusinessException 사용자 없음 시 발생
     */
    public InternalUserInfoResponse execute(UUID userId) {
        log.debug("Internal API 사용자 정보 조회 시작: userId={}", userId);
        
        User user = findUserById(userId);
        DriverProfile driverProfile = findDriverProfileIfDriver(user);
        
        InternalUserInfoResponse response = mapper.toResponse(user, driverProfile);
        
        log.debug("Internal API 사용자 정보 조회 완료: userId={}, role={}", 
                userId, user.getRole());
        
        return response;
    }
    
    /**
     * 사용자 조회 (없으면 예외 발생)
     */
    private User findUserById(UUID userId) {
        return userRepository.findByUserUuid(userId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음: userId={}", userId);
                    return new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND);
                });
    }
    
    /**
     * 기사인 경우만 DriverProfile 조회
     */
    private DriverProfile findDriverProfileIfDriver(User user) {
        if (user.getRole() != UserRole.DRIVER) {
            log.debug("기사가 아닌 사용자: userId={}, role={}", user.getUserUuid(), user.getRole());
            return null;
        }
        
        return driverProfileRepository.findByUserId(user.getUserUuid())
                .orElse(null); // 기사이지만 프로필이 없는 경우는 null 반환
    }

    @Transactional(readOnly = true)
    public List<InternalUserInfoResponse> findByRole(UserRole role) {
        log.debug("Role 기반 사용자 조회 시작: role={}", role);

        List<User> users = userRepository.findByRoleAndStatusAndDeletedAtIsNull(role, UserStatus.ACTIVE);

        return users.stream()
            .map(user -> mapper.toResponse(user, null)) // 기사 프로필은 필요 없음
            .collect(Collectors.toList());
    }
}