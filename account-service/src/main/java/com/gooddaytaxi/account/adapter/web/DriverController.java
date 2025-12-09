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
    
    @Operation(summary = "기사 프로필 조회", description = "특정 기사의 프로필 정보를 조회합니다.")
    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverProfileResponse>> getDriverProfile(
            @PathVariable("driverId") String driverId) {
        
        log.debug("기사 프로필 조회 요청: driverId={}", driverId);
        
        UUID driverUuid = UUID.fromString(driverId);
        DriverProfileResponse response = getDriverProfileUseCase.execute(driverUuid);
        
        log.debug("기사 프로필 조회 완료: driverId={}", driverId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "기사 프로필 조회가 완료되었습니다."));
    }
    
    @Operation(summary = "기사 프로필 수정", description = "기사의 프로필 정보(차량 정보)를 수정합니다.")
    @PatchMapping("/{driverId}")
    public ResponseEntity<ApiResponse<UpdateDriverProfileResponse>> updateDriverProfile(
            @RequestHeader("X-User-UUID") String requestUserUuid,
            @PathVariable("driverId") String driverId,
            @Valid @RequestBody UpdateDriverProfileCommand command) {
        
        log.debug("기사 프로필 수정 요청: requestUser={}, driverId={}", requestUserUuid, driverId);
        
        UUID requestUuid = UUID.fromString(requestUserUuid);
        UUID driverUuid = UUID.fromString(driverId);
        
        UpdateDriverProfileResponse response = updateDriverProfileUseCase.execute(requestUuid, driverUuid, command);
        
        log.debug("기사 프로필 수정 완료: driverId={}", driverId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "기사 프로필 수정이 완료되었습니다."));
    }
    
    @Operation(summary = "기사 상태 변경", description = "기사의 온라인/오프라인 상태를 변경합니다.")
    @PatchMapping("/{driverId}/status")
    public ResponseEntity<ApiResponse<UpdateDriverStatusResponse>> updateDriverStatus(
            @RequestHeader("X-User-UUID") String requestUserUuid,
            @PathVariable("driverId") String driverId,
            @Valid @RequestBody UpdateDriverStatusCommand command) {
        
        log.debug("기사 상태 변경 요청: requestUser={}, driverId={}, status={}", 
                requestUserUuid, driverId, command.getOnlineStatus());
        
        UUID requestUuid = UUID.fromString(requestUserUuid);
        UUID driverUuid = UUID.fromString(driverId);
        
        UpdateDriverStatusResponse response = updateDriverStatusUseCase.execute(requestUuid, driverUuid, command);
        
        log.debug("기사 상태 변경 완료: driverId={}, newStatus={}", driverId, response.getOnlineStatus());
        
        return ResponseEntity.ok(ApiResponse.success(response, "기사 상태 변경이 완료되었습니다."));
    }
}