package com.gooddaytaxi.account.application.usecase;

import com.gooddaytaxi.account.application.dto.AvailableDriversResponse;
import com.gooddaytaxi.account.domain.model.User;
import com.gooddaytaxi.account.domain.model.UserRole;
import com.gooddaytaxi.account.domain.model.UserStatus;
import com.gooddaytaxi.account.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 배차 가능한 기사 목록 조회 UseCase
 * 
 * 현재는 더미 데이터로 구현 (GIS 미적용)
 * 향후 실제 위치 기반 필터링으로 교체 예정
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAvailableDriversUseCase {
    
    private final UserRepository userRepository;
    
    /**
     * 주소 기반 배차 가능 기사 조회
     * 
     * @param pickupAddress 픽업 주소 (현재는 동 단위로 매칭)
     * @return 대기중인 기사 UUID 목록
     */
    public AvailableDriversResponse execute(String pickupAddress) {
        log.debug("배차 가능 기사 조회 시작: pickupAddress={}", pickupAddress);
        
        // 1. 주소에서 지역 추출 (더미 로직)
        String region = extractRegion(pickupAddress);
        
        // 2. 해당 지역의 대기중인 기사 조회
        List<User> availableDrivers = getAvailableDriversByRegion(region);
        
        // 3. UUID 목록으로 변환
        List<UUID> driverIds = availableDrivers.stream()
                .map(User::getUserUuid)
                .toList();
        
        log.debug("배차 가능 기사 조회 완료: region={}, count={}", region, driverIds.size());
        
        return AvailableDriversResponse.builder()
                .driverIds(driverIds)
                .region(region)
                .totalCount(driverIds.size())
                .build();
    }
    
    /**
     * 주소에서 지역 추출 (더미 로직)
     * 실제로는 GIS나 주소 파싱 API 사용
     */
    private String extractRegion(String pickupAddress) {
        if (pickupAddress == null || pickupAddress.isBlank()) {
            return "강남구";  // 기본값
        }
        
        // 간단한 키워드 매칭 (더미)
        if (pickupAddress.contains("강남") || pickupAddress.contains("역삼") || pickupAddress.contains("삼성")) {
            return "강남구";
        } else if (pickupAddress.contains("서초") || pickupAddress.contains("방배") || pickupAddress.contains("잠원")) {
            return "서초구";
        } else if (pickupAddress.contains("송파") || pickupAddress.contains("잠실") || pickupAddress.contains("방이")) {
            return "송파구";
        } else if (pickupAddress.contains("마포") || pickupAddress.contains("홍대") || pickupAddress.contains("합정")) {
            return "마포구";
        }
        
        return "강남구";  // 기본값
    }
    
    /**
     * 지역별 대기중인 기사 조회 (더미 로직)
     * 실제로는 위치 기반 쿼리 사용
     */
    private List<User> getAvailableDriversByRegion(String region) {
        // 모든 활성화된 기사 조회
        List<User> allDrivers = userRepository.findByRoleAndStatusAndDeletedAtIsNull(
                UserRole.DRIVER, UserStatus.ACTIVE);
        
        // 지역별 더미 필터링 (실제로는 위치 기반)
        return allDrivers.stream()
                .filter(driver -> isDriverInRegion(driver, region))
                .limit(getMaxDriverCountByRegion(region))  // 지역별 최대 기사 수 제한
                .toList();
    }
    
    /**
     * 기사가 해당 지역에 있는지 확인 (더미 로직)
     */
    private boolean isDriverInRegion(User driver, String region) {
        // 더미로 일부 기사들만 특정 지역에 배정
        String driverIdStr = driver.getUserUuid().toString();
        int hash = Math.abs(driverIdStr.hashCode()) % 4;
        
        return switch (region) {
            case "강남구" -> hash == 0;
            case "서초구" -> hash == 1; 
            case "송파구" -> hash == 2;
            case "마포구" -> hash == 3;
            default -> hash == 0;
        };
    }
    
    /**
     * 지역별 최대 반환 기사 수
     */
    private long getMaxDriverCountByRegion(String region) {
        return switch (region) {
            case "강남구" -> 5;
            case "서초구" -> 3;
            case "송파구" -> 4;
            case "마포구" -> 2;
            default -> 3;
        };
    }
}