package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.DispatchDriverInfoResponse;
import com.gooddaytaxi.account.application.mapper.DispatchDriverInfoMapper;
import com.gooddaytaxi.account.domain.exception.AccountBusinessException;
import com.gooddaytaxi.account.domain.exception.AccountErrorCode;
import com.gooddaytaxi.account.domain.model.DriverProfile;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.repository.DriverProfileRepository;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Dispatch 서비스용 기사 정보 조회 UseCase
 * 
 * Single Responsibility: 배차 시스템 전용 기사 정보 조회 로직만 담당
 * Open/Closed: 새로운 Dispatch API 요구사항이 생기면 새 UseCase를 추가
 * Dependency Inversion: Repository와 Mapper 인터페이스에 의존
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDispatchDriverInfoUseCase {
    
    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final DispatchDriverInfoMapper mapper;
    
    /**
     * 배차용 기사 정보 조회
     * 
     * @param driverId 조회할 기사 ID
     * @return Dispatch용 기사 정보
     * @throws AccountBusinessException 기사 없음, 권한 오류 시 발생
     */
    public DispatchDriverInfoResponse execute(UUID driverId) {
        log.debug("배차용 기사 정보 조회 시작: driverId={}", driverId);
        
        User driver = findDriverById(driverId);
        validateDriverRole(driver);
        
        DriverProfile driverProfile = findDriverProfile(driver);
        
        DispatchDriverInfoResponse response = mapper.toResponse(driver, driverProfile);
        
        log.debug("배차용 기사 정보 조회 완료: driverId={}, status={}, onlineStatus={}", 
                driverId, driver.getStatus(), driverProfile != null ? driverProfile.getOnlineStatus() : "UNKNOWN");
        
        return response;
    }
    
    /**
     * 기사 사용자 조회 (삭제되지 않은 사용자만)
     */
    private User findDriverById(UUID driverId) {
        return userRepository.findByUserUuid(driverId)
                .filter(user -> user.getDeletedAt() == null) // 삭제된 사용자 제외
                .orElseThrow(() -> {
                    log.error("기사를 찾을 수 없음: driverId={}", driverId);
                    return new AccountBusinessException(AccountErrorCode.USER_NOT_FOUND);
                });
    }
    
    /**
     * 기사 권한 검증
     */
    private void validateDriverRole(User user) {
        if (user.getRole() != UserRole.DRIVER) {
            log.warn("기사가 아닌 사용자에 대한 배차 API 호출: userId={}, role={}", 
                    user.getUserUuid(), user.getRole());
            throw new AccountBusinessException(AccountErrorCode.ACCESS_DENIED);
        }
    }
    
    /**
     * 기사 프로필 조회 (없어도 예외 발생하지 않음)
     */
    private DriverProfile findDriverProfile(User driver) {
        return driverProfileRepository.findByUserId(driver.getUserUuid())
                .orElse(null); // 프로필이 없어도 기본 정보는 제공
    }
}