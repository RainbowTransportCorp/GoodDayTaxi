package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.commend.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.result.DispatchCancelResult;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.result.DispatchDetailResult;
import com.gooddaytaxi.dispatch.application.result.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.application.service.PassengerDispatchService;
import com.gooddaytaxi.dispatch.application.validator.UserRole;
import com.gooddaytaxi.dispatch.presentation.external.dto.request.DispatchCreateRequestDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCancelResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCreateResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchDetailResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchListResponseDto;
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

    private final PassengerDispatchService passengerDispatchService;

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
        DispatchCreateResult createResult = passengerDispatchService.create(createCommand);
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
            @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String role

    ) {
        List<DispatchSummaryResult> summaries =
                passengerDispatchService.getDispatchList(userId, UserRole.valueOf(role));

        List<DispatchListResponseDto> response =
                DispatchListResponseMapper.toDispatchListResponseList(summaries);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * 콜 상세조회(승객)
     * 감사에 대한 uuid는 자동으로 들어가고 있으므로 uuid는 헤더에서 생략하고
     * role정보만 헤더에서 받아옵니다.
     *
     * @param dispatchId
     * @return
     */
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<DispatchDetailResponseDto>> getDispatchDetail(
            @PathVariable UUID dispatchId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        DispatchDetailResult dispatchDetailResult = passengerDispatchService.getDispatchDetail(UserRole.valueOf(role), dispatchId);
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
            @RequestHeader(value = "X-User-UUID", required = false) UUID userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        DispatchCancelCommand command = new DispatchCancelCommand(dispatchId);
        DispatchCancelResult result = passengerDispatchService.cancel(command);
        DispatchCancelResponseDto responseDto = DispatchCancelResponseMapper.toCancelResponse(result);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}