package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.commend.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.result.DispatchCancelResult;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.result.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.result.DispatchListResult;
import com.gooddaytaxi.dispatch.application.service.DispatchService;
import com.gooddaytaxi.dispatch.presentation.external.dto.request.DispatchCreateRequestDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.*;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchCreateCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchCancelResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchCreateResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchDetailResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchListResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispatches")
public class DispatchController {

    private final DispatchService dispatchService;

    /**
     * 콜 생성 (승객)
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DispatchCreateResponseDto>> create(
            @RequestBody DispatchCreateRequestDto requestDto
//            ,@RequestHeader(value = "x-user-UUID", required = false) UUID userId,
//            @RequestHeader(value = "x-user-role", required = false) String role
            ) {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        DispatchCreateCommand createCommand = DispatchCreateCommandMapper.toCommand(requestDto);
        DispatchCreateResult createResult = dispatchService.create(userId, createCommand);
        DispatchCreateResponseDto responseDto = DispatchCreateResponseMapper.toCreateResponse(createResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    /**
     * 콜 전체 조회 (승객)
     * @return
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DispatchListResponseDto>> getDispatchs (
            //            ,@RequestHeader(value = "x-user-uuid", required = false) UUID userId
    ) {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        DispatchListResult dispatchListResult = dispatchService.getDispatchList(userId);
        DispatchListResponseDto responseDto = DispatchListResponseMapper.toDispatchListResponse(dispatchListResult);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 콜 상세조회(승객)
     * @param dispatchId
     * @return
     */
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<DispatchDetailResponseDto>> getDispatchDetail (
            @PathVariable UUID dispatchId
            //            ,@RequestHeader(value = "x-user-uuid", required = false) UUID userId
    ) {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        DispatchDetailResult dispatchDetailResult = dispatchService.getDispatchDetail(userId);
        DispatchDetailResponseDto responseDto = DispatchDetailResponseMapper.toDispatchDetailResponse(dispatchDetailResult);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 콜 취소 (승객)
     * @param dispatchId
     * @return
     */
    @PatchMapping("/{dispatchId}/cancel")
    public ResponseEntity<ApiResponse<DispatchCancelResponseDto>> cancel(
            @PathVariable UUID dispatchId
            //            ,@RequestHeader(value = "x-user-uuid", required = false) UUID userId
    ) {
        DispatchCancelCommand command = new DispatchCancelCommand(dispatchId);
        DispatchCancelResult result = dispatchService.cancel(command);
        DispatchCancelResponseDto responseDto = DispatchCancelResponseMapper.toCancelResponse(result);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}