package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.AvailableDriversResponse;
import com.gooddaytaxi.account.application.dto.DispatchDriverInfoResponse;
import com.gooddaytaxi.account.application.dto.InternalUserInfoResponse;
import com.gooddaytaxi.account.application.usecase.GetAvailableDriversUseCase;
import com.gooddaytaxi.account.application.usecase.GetDispatchDriverInfoUseCase;
import com.gooddaytaxi.account.application.usecase.GetInternalUserInfoUseCase;
import com.gooddaytaxi.account.domain.model.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
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
@RequestMapping("/internal/v1/account") //내부 호출에는 api를 생략하고 바로 버전을 표기합니다.
@RequiredArgsConstructor
public class InternalUserController {
    
    private final GetInternalUserInfoUseCase getInternalUserInfoUseCase;
    private final GetAvailableDriversUseCase getAvailableDriversUseCase;
    private final GetDispatchDriverInfoUseCase getDispatchDriverInfoUseCase;
    
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
    
    @Operation(summary = "기사 정보 조회", description = "마이크로서비스 간 통신용 API. 배차 시 기사 정보(이름, 차량정보) 조회용. 콜 수락 알림 등에서 사용.")
    @GetMapping("/drivers/{userId}")
    public ResponseEntity<DispatchDriverInfoResponse> getDriverInfo(
            @Parameter(description = "조회할 기사 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440002")
            @PathVariable UUID userId) {
        
        log.debug("Internal API 기사 정보 조회 요청: userId={}", userId);
        
        DispatchDriverInfoResponse response = getDispatchDriverInfoUseCase.execute(userId);
        
        log.debug("Internal API 기사 정보 조회 성공: userId={}, name={}", 
                userId, response.getName());
        
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "MASTER_ADMIN 전체 UUID 조회",
        description = "MASTER_ADMIN 역할을 가진 사용자들의 UUID 리스트를 반환합니다. support-service에서 알림 등 용도로 사용."
    )
    @GetMapping("/admins/uuids")
    public ResponseEntity<List<UUID>> getMasterAdminUuids() {
        // MASTER_ADMIN 역할 사용자 조회
        List<InternalUserInfoResponse> admins = getInternalUserInfoUseCase.findByRole(UserRole.MASTER_ADMIN);


        // UUID만 추출
        List<UUID> uuids = admins.stream()
            .map(InternalUserInfoResponse::getUserId)
            .collect(Collectors.toList());

        log.debug("MASTER_ADMIN UUID 조회 완료: count={}", uuids.size());
        return ResponseEntity.ok(uuids);
    }
}