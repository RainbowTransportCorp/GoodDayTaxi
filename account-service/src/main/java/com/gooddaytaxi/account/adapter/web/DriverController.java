package com.gooddaytaxi.account.adapter.web;

import com.gooddaytaxi.account.application.dto.DriverProfileResponse;
import com.gooddaytaxi.account.application.dto.UpdateDriverProfileCommand;
import com.gooddaytaxi.account.application.dto.UpdateDriverProfileResponse;
import com.gooddaytaxi.account.application.dto.UpdateDriverStatusCommand;
import com.gooddaytaxi.account.application.dto.UpdateDriverStatusResponse;
import com.gooddaytaxi.account.application.usecase.GetDriverProfileUseCase;
import com.gooddaytaxi.account.application.usecase.UpdateDriverProfileUseCase;
import com.gooddaytaxi.account.application.usecase.UpdateDriverStatusUseCase;
import com.gooddaytaxi.common.core.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "기사 관리", description = "기사 프로필 조회 및 상태 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {
    
    private final GetDriverProfileUseCase getDriverProfileUseCase;
    private final UpdateDriverProfileUseCase updateDriverProfileUseCase;
    private final UpdateDriverStatusUseCase updateDriverStatusUseCase;
    
    @Operation(summary = "기사 프로필 조회", description = "특정 기사의 프로필 정보(차량 정보, 온라인 상태 등)를 조회합니다. 모든 사용자가 조회 가능합니다.")
    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> getDriverProfile(
            @Parameter(description = "기사 UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440002")
            @PathVariable("driverId") String driverId) {
        
        log.debug("기사 프로필 조회 요청: driverId={}", driverId);
        
        UUID driverUuid = UUID.fromString(driverId);
        DriverProfileResponse response = getDriverProfileUseCase.execute(driverUuid);
        
        log.debug("기사 프로필 조회 완료: driverId={}", driverId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "기사 프로필 조회가 완료되었습니다."));
    }
    
    @Operation(summary = "내 프로필 수정", description = "현재 로그인된 기사의 차량 정보(차량번호, 차량종류, 차량색상)를 수정합니다. 기사 전용 API입니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateDriverProfileResponse>> updateMyDriverProfile(
            @Parameter(description = "기사 UUID (현재 로그인된 기사)", required = true, example = "550e8400-e29b-41d4-a716-446655440002")
            @RequestHeader("X-User-UUID") String userUuid,
            @Valid @RequestBody UpdateDriverProfileCommand command) {
        
        log.debug("기사 프로필 수정 요청: userUuid={}", userUuid);
        
        UUID requestUuid = UUID.fromString(userUuid);
        
        UpdateDriverProfileResponse response = updateDriverProfileUseCase.execute(requestUuid, requestUuid, command);
        
        log.debug("기사 프로필 수정 완료: userUuid={}", userUuid);
        
        return ResponseEntity.ok(ApiResponse.success(response, "내 프로필 수정이 완료되었습니다."));
    }
    
    @Operation(summary = "내 상태 변경", description = "현재 로그인된 기사의 온라인/오프라인 상태를 변경합니다. 기사 전용 API로 배차 가능 상태를 조절합니다.")
    @PatchMapping("/me/status")
    public ResponseEntity<ApiResponse<UpdateDriverStatusResponse>> updateMyDriverStatus(
            @Parameter(description = "기사 UUID (현재 로그인된 기사)", required = true, example = "550e8400-e29b-41d4-a716-446655440002")
            @RequestHeader("X-User-UUID") String userUuid,
            @Valid @RequestBody UpdateDriverStatusCommand command) {
        
        log.debug("기사 상태 변경 요청: userUuid={}, status={}", 
                userUuid, command.getOnlineStatus());
        
        UUID requestUuid = UUID.fromString(userUuid);
        
        UpdateDriverStatusResponse response = updateDriverStatusUseCase.execute(requestUuid, requestUuid, command);
        
        log.debug("기사 상태 변경 완료: userUuid={}, newStatus={}", userUuid, response.getOnlineStatus());
        
        return ResponseEntity.ok(ApiResponse.success(response, "내 상태 변경이 완료되었습니다."));
    }
}