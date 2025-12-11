package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.application.query.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectResult;
import com.gooddaytaxi.dispatch.application.service.DriverDispatchService;
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

    private final DriverDispatchService driverDispatchService;

    /**
     * (기사) 배차 시도 중인 콜 목록 조회
     * @return
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DispatchPendingListResponseDto>>> getPendingDispatches(
            @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        List<DispatchPendingListResult> dispatchPendingListResults =
                driverDispatchService.getDriverPendingDispatch(userId);

        List<DispatchPendingListResponseDto> responseDto =
                DispatchPendingListResponseMapper.toDtoList(dispatchPendingListResults);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * (기사) 콜 수락
     * @param dispatchId
     * @return
     */
    @PatchMapping("/{dispatchId}/accept")
    public ResponseEntity<ApiResponse<DispatchAcceptResponseDto>> accept(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) throws InterruptedException {
        DispatchAcceptCommand command = DispatchAcceptCommandMapper.toCommand(userId, role, dispatchId);
        DispatchAcceptResult result = driverDispatchService.accept(command);
        DispatchAcceptResponseDto responseDto = DispatchAcceptResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * (기사) 콜 거절
     * @param dispatchId
     * @return
     */
    @PatchMapping("/{dispatchId}/reject")
    public ResponseEntity<ApiResponse<DispatchRejectResponseDto>> reject(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        DispatchRejectCommand command = DispatchRejectCommandMapper.toCommand(userId, role, dispatchId);
        DispatchRejectResult result = driverDispatchService.reject(command);
        DispatchRejectResponseDto responseDto = DispatchRejectResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

}
