package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.service.passenger.DispatchCancelService;
import com.gooddaytaxi.dispatch.application.service.passenger.DispatchCreateService;
import com.gooddaytaxi.dispatch.application.service.passenger.PassengerDispatchQueryService;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelResult;
import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.query.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.query.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.exception.auth.UserRole;
import com.gooddaytaxi.dispatch.presentation.external.dto.request.DispatchCreateRequestDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCancelResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCreateResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchDetailResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchListResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchCancelCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchCreateCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchCancelResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchCreateResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchDetailResponseMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchListResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispatches")
public class DispatchController {

    private final PassengerDispatchQueryService passengerDispatchQueryService;
    private final DispatchCreateService dispatchCreateService;
    private final DispatchCancelService dispatchCancelService;

    /**
     * 콜 생성 (승객)
     *
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DispatchCreateResponseDto>> create(
            @RequestBody DispatchCreateRequestDto requestDto,
            @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        DispatchCreateCommand createCommand = DispatchCreateCommandMapper.toCommand(userId, role, requestDto);
        DispatchCreateResult createResult = dispatchCreateService.create(createCommand);
        DispatchCreateResponseDto responseDto = DispatchCreateResponseMapper.toCreateResponse(createResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    /**
     * 콜 전체 조회 (승객)
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DispatchListResponseDto>>> getDispatches(
            @RequestHeader(value = "X-User-UUID", required = false) UUID passengerId,
            @RequestHeader(value = "X-User-Role", required = false) String role

    ) {
        List<DispatchSummaryResult> summaries =
                passengerDispatchQueryService.getDispatchList(passengerId, UserRole.valueOf(role));

        List<DispatchListResponseDto> response =
                DispatchListResponseMapper.toDispatchListResponseList(summaries);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * 콜 상세조회(승객)
     *
     * @param dispatchId
     * @return
     */
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<DispatchDetailResponseDto>> getDispatchDetail(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-UUID", required = false) UUID passengerId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        DispatchDetailResult dispatchDetailResult = passengerDispatchQueryService.getDispatchDetail(passengerId, UserRole.valueOf(role), dispatchId);
        DispatchDetailResponseDto responseDto = DispatchDetailResponseMapper.toDispatchDetailResponse(dispatchDetailResult);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 콜 취소 (승객)
     *
     * @param dispatchId
     * @return
     */
    @PatchMapping("/{dispatchId}/cancel")
    public ResponseEntity<ApiResponse<DispatchCancelResponseDto>> cancel(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-UUID", required = false) UUID passengerId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        DispatchCancelCommand command = DispatchCancelCommandMapper.toCommand(passengerId, role, dispatchId);
        DispatchCancelResult result = dispatchCancelService.cancel(command);
        DispatchCancelResponseDto responseDto = DispatchCancelResponseMapper.toCancelResponse(result);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}