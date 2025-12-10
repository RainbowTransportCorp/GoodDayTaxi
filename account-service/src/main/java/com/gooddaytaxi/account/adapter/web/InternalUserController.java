package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.AvailableDriversResponse;
import com.gooddaytaxi.account.application.dto.InternalUserInfoResponse;
import com.gooddaytaxi.account.application.usecase.GetAvailableDriversUseCase;
import com.gooddaytaxi.account.application.usecase.GetInternalUserInfoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal API용 사용자 정보 조회 컨트롤러
 * 
 * - 외부 노출되지 않는 서비스 간 통신용 API
 * - support-service 등에서 사용자 정보 조회 시 사용
 *
 */
@Tag(name = "내부 API", description = "서비스 간 통신용 내부 API")
@Slf4j
@RestController
@RequestMapping("/internal/api/v1")
@RequiredArgsConstructor
public class InternalUserController {
    
    private final GetInternalUserInfoUseCase getInternalUserInfoUseCase;
    private final GetAvailableDriversUseCase getAvailableDriversUseCase;
    
    @Operation(summary = "사용자 정보 조회 (내부)", description = "마이크로서비스 간 통신용 API. 특정 사용자(승객/기사/관리자)의 정보를 조회합니다. support-service 등에서 사용, 슬랙 알림용 데이터 포함.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<InternalUserInfoResponse> getUserInfo(
            @Parameter(description = "조회할 사용자 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440001")
            @PathVariable UUID userId) {
        
        log.debug("Internal API 사용자 정보 조회 요청: userId={}", userId);
        
        InternalUserInfoResponse response = getInternalUserInfoUseCase.execute(userId);
        
        log.debug("Internal API 사용자 정보 조회 성공: userId={}, role={}", 
                userId, response.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "배차 가능한 기사 목록 조회", description = "마이크로서비스 간 통신용 API. 픽업 주소를 기반으로 온라인 상태인 기사들의 UUID 목록을 반환합니다. dispatch-service에서 사용, 더미 지역 매핑 적용.")
    @GetMapping("/drivers/available")
    public ResponseEntity<AvailableDriversResponse> getAvailableDrivers(
            @Parameter(description = "픽업 주소", required = true, example = "서울 강남구 역삼동")
            @RequestParam String pickupAddress) {
        
        log.debug("배차 가능 기사 조회 요청: pickupAddress={}", pickupAddress);
        
        AvailableDriversResponse response = getAvailableDriversUseCase.execute(pickupAddress);
        
        log.debug("배차 가능 기사 조회 완료: region={}, count={}", 
                response.getRegion(), response.getTotalCount());
        
        return ResponseEntity.ok(response);
    }
}