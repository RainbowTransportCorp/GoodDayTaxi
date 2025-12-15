package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.service.admin.AdminDispatchQueryService;
import com.gooddaytaxi.dispatch.application.service.admin.AdminDispatchService;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutCommand;
import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutResult;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminForceTimeoutResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.AdminDispatchResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/dispatches")
@RequiredArgsConstructor
public class AdminDispatchController {

    private final AdminDispatchQueryService queryService;
    private final AdminDispatchService adminService;

    @GetMapping
    public ApiResponse<?> getDispatches(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestParam(required = false) DispatchStatus status
    ) {
        return ApiResponse.success(queryService.findAll(UserRole.valueOf(role), status));
    }

    @GetMapping("/{dispatchId}")
    public ApiResponse<?> getDispatchDetail(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID dispatchId
    ) {
        return ApiResponse.success(queryService.findDetail(UserRole.valueOf(role), dispatchId));
    }

    @PatchMapping("/{dispatchId}/force-timeout")
    public ResponseEntity<ApiResponse<AdminForceTimeoutResponseDto>> forceTimeout(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable UUID dispatchId
    ) {
        AdminForceTimeoutCommand command = new AdminForceTimeoutCommand("응답 이상으로 인한 강제 Timeout");
        AdminForceTimeoutResult result = adminService.forceTimeout(UserRole.valueOf(role), dispatchId, command);
        AdminForceTimeoutResponseDto responseDto = AdminDispatchResponseMapper.toResponse(result);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}
