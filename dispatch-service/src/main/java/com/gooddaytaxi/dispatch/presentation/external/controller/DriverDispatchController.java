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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispatches/driver")
public class DriverDispatchController {

    private final DriverDispatchQueryService driverDispatchQueryService;
    private final DispatchAcceptService dispatchAcceptService;
    private final DispatchRejectService dispatchRejectService;

    /**
     * 배차 후보기사에게 할당된 콜 목록 조회
     * @param userId 토큰을 게이트웨이로 보내서 파싱 후 가져온 헤더의 userId
     * @param role 토큰을 게이트웨이로 보내서 파싱 후 가져온 헤더의 role
     * @return 후보기사 본인에게 할당된 콜(배차) 리스트
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DispatchPendingListResponseDto>>> getPendingDispatches(
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @RequestHeader(value = "X-User-Role") String role
    ) {
        List<DispatchPendingListResult> dispatchPendingListResults =
                driverDispatchQueryService.getDriverPendingDispatch(userId, UserRole.valueOf(role));

        List<DispatchPendingListResponseDto> responseDto =
                DispatchPendingListResponseMapper.toDtoList(dispatchPendingListResults);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 배차 후보 기사가 특정 콜에 대한 배차를 수락
     * @param dispatchId 수락하기 위한 특정 배차의 식별자
     * @param userId 게이트웨이 헤더에서 받은 uuid
     * @param role 게이트웨이 헤더에서 받은 role
     * @return 수락된 배차정보
     */
    @PatchMapping("/{dispatchId}/accept")
    public ResponseEntity<ApiResponse<DispatchAcceptResponseDto>> accept(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @RequestHeader(value = "X-User-Role") String role
    )  {
        DispatchAcceptCommand command = DispatchAcceptCommandMapper.toCommand(userId, role, dispatchId);
        DispatchAcceptResult result = dispatchAcceptService.accept(command);
        DispatchAcceptResponseDto responseDto = DispatchAcceptResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 배차 후보 기사가 특정 콜에 대한 배차를 거절
     * @param dispatchId 거절하기 위한 특정 배차의 식별자
     * @param userId 헤더의 uuid
     * @param role 헤더의 role
     * @return 거절된 배차정보
     */
    @PatchMapping("/{dispatchId}/reject")
    public ResponseEntity<ApiResponse<DispatchRejectResponseDto>> reject(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @RequestHeader(value = "X-User-Role") String role
    ) {
        DispatchRejectCommand command = DispatchRejectCommandMapper.toCommand(userId, role, dispatchId);
        DispatchRejectResult result = dispatchRejectService.reject(command);
        DispatchRejectResponseDto responseDto = DispatchRejectResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

}
