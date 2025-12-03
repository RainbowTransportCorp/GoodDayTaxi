package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.commend.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.result.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.application.result.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.application.service.DriverDispatchService;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchAcceptResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchPendingListResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchAcceptCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchAcceptResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchPendingListResponseMapper;
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
            //            ,@RequestHeader(value = "x-user-uuid", required = false) UUID userId
    ) {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000011");
        List<DispatchPendingListResult> dispatchPendingListResults =
                driverDispatchService.getDriverPendingDispatch(userId);

        List<DispatchPendingListResponseDto> responseDto =
                DispatchPendingListResponseMapper.toDtoList(dispatchPendingListResults);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @PatchMapping("/{dispatchId}/accept")
    public ResponseEntity<ApiResponse<DispatchAcceptResponseDto>> accept(
            @PathVariable UUID dispatchId
            //            ,@RequestHeader(value = "x-user-uuid", required = false) UUID userId
    ) {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000011");
        DispatchAcceptCommand command = DispatchAcceptCommandMapper.toCommand(userId, dispatchId);
        DispatchAcceptResult result = driverDispatchService.accept(command);
        DispatchAcceptResponseDto responseDto = DispatchAcceptResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

}
