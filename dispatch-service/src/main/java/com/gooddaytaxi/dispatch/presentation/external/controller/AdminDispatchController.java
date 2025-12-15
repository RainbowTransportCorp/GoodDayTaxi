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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/dispatches")
@RequiredArgsConstructor
public class AdminDispatchController {

    private final AdminDispatchQueryService adminQueryService;
    private final AdminDispatchService adminService;

    /**
     * 관리자 배차 목록 조회 (ADMIN, MASTER_ADMIN)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminDispatchListResponseDto>>> getDispatches(
            @RequestHeader(value = "X-User-Role", required = false) String role,
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
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<AdminDispatchDetailResponseDto>> getDispatchDetail(
            @RequestHeader(value = "X-User-Role", required = false) String role,
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
     * 관리자 강제 Timeout 처리 (MASTER_ADMIN)
     */
    @PatchMapping("/{dispatchId}/force-timeout")
    public ResponseEntity<ApiResponse<AdminForceTimeoutResponseDto>> forceTimeout(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID dispatchId
    ) {
        UserRole userRole = UserRole.valueOf(role);

        AdminForceTimeoutCommand command =
                new AdminForceTimeoutCommand("응답 이상으로 인한 강제 Timeout");

        AdminForceTimeoutResult result =
                adminService.forceTimeout(userRole, dispatchId, command);

        AdminForceTimeoutResponseDto response =
                AdminTimeoutResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
