package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.DispatchDriverInfoResponse;
import com.gooddaytaxi.account.application.dto.InternalUserInfoResponse;
import com.gooddaytaxi.account.application.usecase.GetDispatchDriverInfoUseCase;
import com.gooddaytaxi.account.application.usecase.GetInternalUserInfoUseCase;
import com.gooddaytaxi.common.core.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Internal API용 사용자 정보 조회 컨트롤러
 * 
 * - 외부 노출되지 않는 서비스 간 통신용 API
 * - support-service 등에서 사용자 정보 조회 시 사용
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class InternalUserController {
    
    private final GetInternalUserInfoUseCase getInternalUserInfoUseCase;
    private final GetDispatchDriverInfoUseCase getDispatchDriverInfoUseCase;
    
    /**
     * 사용자 정보 조회 (Internal API)
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (슬랙 알림 등에 필요한 정보 포함)
     *
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<InternalUserInfoResponse> getUserInfo(
            @PathVariable UUID userId) {
        
        log.debug("Internal API 사용자 정보 조회 요청: userId={}", userId);
        
        InternalUserInfoResponse response = getInternalUserInfoUseCase.execute(userId);
        
        log.debug("Internal API 사용자 정보 조회 성공: userId={}, role={}", 
                userId, response.getRole());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 기사 정보 조회 (Dispatch API)
     * 
     * @param driverId 조회할 기사 ID
     * @return 배차용 기사 정보
     */
    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<ApiResponse<DispatchDriverInfoResponse>> getDriverInfo(
            @PathVariable UUID driverId) {
        
        log.debug("Dispatch API 기사 정보 조회 요청: driverId={}", driverId);
        
        DispatchDriverInfoResponse response = getDispatchDriverInfoUseCase.execute(driverId);
        
        log.debug("Dispatch API 기사 정보 조회 성공: driverId={}, status={}", 
                driverId, response.getStatus());
        
        return ResponseEntity.ok(ApiResponse.success(response, "기사 정보 조회가 완료되었습니다."));
    }
}