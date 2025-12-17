package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.application.service.driver.DispatchAcceptService;
import com.gooddaytaxi.dispatch.application.service.driver.DispatchRejectService;
import com.gooddaytaxi.dispatch.application.service.driver.DriverDispatchQueryService;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.application.query.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchAcceptResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchPendingListResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchRejectResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchAcceptCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchRejectCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchAcceptResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchPendingListResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchRejectResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


/**
 * 기사(Driver) 전용 배차 API 컨트롤러.
 *
 * 모든 요청은 Gateway에서 인증된 사용자 정보를 헤더로 전달받는다.
 * - X-User-UUID : 요청 사용자 식별자
 * - X-User-Role : 요청 사용자 역할
 */
@SecurityRequirement(name = "userId")
@SecurityRequirement(name = "role")
@Tag(
        name = "Driver Dispatch API",
        description = "기사(Driver)가 배차를 조회·수락·거절하는 외부 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispatches/driver")
public class DriverDispatchController {

    private final DriverDispatchQueryService driverDispatchQueryService;
    private final DispatchAcceptService dispatchAcceptService;
    private final DispatchRejectService dispatchRejectService;

    /**
     * 배차 후보기사에게 할당된 배차 목록 조회
     * @param userId 요청 기사 UUID
     * @param role 요청 기사 역할
     * @return 후보기사 본인에게 할당된 배차 리스트
     */
    @Operation(
            summary = "배차 대기 목록 조회",
            description = "후보기사에게 할당된 배차 목록을 조회합니다."
    )
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DispatchPendingListResponseDto>>> getPendingDispatches(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role
    ) {
        List<DispatchPendingListResult> dispatchPendingListResults =
                driverDispatchQueryService.getDriverPendingDispatch(userId, UserRole.valueOf(role));

        List<DispatchPendingListResponseDto> responseDto =
                DispatchPendingListResponseMapper.toDtoList(dispatchPendingListResults);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 배차 후보 기사가 특정 배차에 대해 수락
     * @param dispatchId 수락하기 위한 특정 배차의 식별자
     * @param userId 요청 기사 UUID
     * @param role 요청 기사 역할
     * @return 수락된 배차정보
     */
    @Operation(
            summary = "배차 수락",
            description = "배차 후보 기사가 특정 배차를 수락합니다."
    )
    @PatchMapping("/{dispatchId}/accept")
    public ResponseEntity<ApiResponse<DispatchAcceptResponseDto>> accept(
            @PathVariable UUID dispatchId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role
    )  {
        DispatchAcceptCommand command = DispatchAcceptCommandMapper.toCommand(userId, role, dispatchId);
        DispatchAcceptResult result = dispatchAcceptService.accept(command);
        DispatchAcceptResponseDto responseDto = DispatchAcceptResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 배차 후보 기사가 특정 배차를 거절
     * @param dispatchId 거절하기 위한 특정 배차의 식별자
     * @param userId 요청 기사 UUID
     * @param role 요청 기사 역할
     * @return 거절된 배차정보
     */
    @Operation(
            summary = "배차 거절",
            description = "배차 후보 기사가 특정 배차를 거절합니다."
    )
    @PatchMapping("/{dispatchId}/reject")
    public ResponseEntity<ApiResponse<DispatchRejectResponseDto>> reject(
            @PathVariable UUID dispatchId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role
    ) {
        DispatchRejectCommand command = DispatchRejectCommandMapper.toCommand(userId, role, dispatchId);
        DispatchRejectResult result = dispatchRejectService.reject(command);
        DispatchRejectResponseDto responseDto = DispatchRejectResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

}
