package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.service.admin.AdminDispatchQueryService;
import com.gooddaytaxi.dispatch.application.service.admin.AdminDispatchService;
import com.gooddaytaxi.dispatch.application.usecase.admin.AdminDispatchDetailResult;
import com.gooddaytaxi.dispatch.application.usecase.admin.AdminDispatchListResult;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutCommand;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutResult;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminDispatchDetailResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminDispatchListResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminForceTimeoutResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.AdminDispatchResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.AdminTimeoutResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/dispatches")
@RequiredArgsConstructor
@Tag(
        name = "Admin Dispatch API",
        description = "관리자(Admin) 배차 조회 및 강제 처리 API"
)
public class AdminDispatchController {

    private final AdminDispatchQueryService adminQueryService;
    private final AdminDispatchService adminService;

    /**
     * 관리자 배차 목록 조회 (ADMIN, MASTER_ADMIN)
     */
    @Operation(
            summary = "관리자 배차 목록 조회",
            description = "관리자가 배차 목록을 조회합니다. 상태 필터링이 가능합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminDispatchListResponseDto>>> getDispatches(
            @RequestHeader(value = "X-User-Role") String role,
            @RequestParam(required = false) DispatchStatus status
    ) {
        UserRole userRole = UserRole.valueOf(role);

        List<AdminDispatchListResult> results =
                adminQueryService.getDispatches(userRole, status);

        List<AdminDispatchListResponseDto> response =
                AdminDispatchResponseMapper.toListResponses(results);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 관리자 배차 상세 조회 (ADMIN, MASTER_ADMIN)
     */
    @Operation(
            summary = "관리자 배차 상세 조회",
            description = "관리자가 특정 배차의 상세 정보를 조회합니다."
    )
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<AdminDispatchDetailResponseDto>> getDispatchDetail(
            @RequestHeader(value = "X-User-Role") String role,
            @PathVariable UUID dispatchId
    ) {
        UserRole userRole = UserRole.valueOf(role);

        AdminDispatchDetailResult result =
                adminQueryService.getDispatchDetail(userRole, dispatchId);

        AdminDispatchDetailResponseDto response =
                AdminDispatchResponseMapper.toDetailResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 관리자 강제 timeout 처리 (cancel로 하려 했으나 timeout이 적절하다고 판단하여 timeout으로 변경)
     * @param role
     * @param dispatchId
     * @return
     */
    @Operation(
            summary = "관리자 강제 Timeout 처리",
            description = "관리자가 특정 배차를 강제로 Timeout 처리합니다."
    )
    @PatchMapping("/{dispatchId}/force-timeout")
    public ResponseEntity<ApiResponse<AdminForceTimeoutResponseDto>> forceTimeout(
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @RequestHeader(value = "X-User-Role") String role,
            @PathVariable UUID dispatchId
    ) {
        UserRole userRole = UserRole.valueOf(role);

        AdminForceTimeoutCommand command =
                new AdminForceTimeoutCommand(userId,"응답 이상으로 인한 강제 Timeout");

        AdminForceTimeoutResult result =
                adminService.forceTimeout(userRole, dispatchId, command);

        AdminForceTimeoutResponseDto response =
                AdminTimeoutResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
