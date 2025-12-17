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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Passenger Dispatch API",
        description = "승객(Passenger) 전용 콜 생성·조회·취소 API"
)
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
    @Operation(
            summary = "콜 생성",
            description = "승객이 새로운 콜(배차 요청)을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<DispatchCreateResponseDto>> create(
            @RequestBody DispatchCreateRequestDto requestDto,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID userId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role
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
    @Operation(
            summary = "콜 전체 조회",
            description = "승객이 본인이 생성한 콜 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<DispatchListResponseDto>>> getDispatches(
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID passengerId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role

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
    @Operation(
            summary = "콜 상세 조회",
            description = "승객이 특정 콜의 상세 정보를 조회합니다."
    )
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<DispatchDetailResponseDto>> getDispatchDetail(
            @PathVariable UUID dispatchId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID passengerId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role
    ) {
        DispatchDetailResult dispatchDetailResult =
                passengerDispatchQueryService.getDispatchDetail(
                        passengerId,
                        UserRole.valueOf(role),
                        dispatchId
                );

        DispatchDetailResponseDto responseDto =
                DispatchDetailResponseMapper.toDispatchDetailResponse(dispatchDetailResult);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 콜 취소 (승객)
     *
     * @param dispatchId
     * @return
     */
    @Operation(
            summary = "콜 취소",
            description = "승객이 생성한 콜을 취소합니다."
    )
    @PatchMapping("/{dispatchId}/cancel")
    public ResponseEntity<ApiResponse<DispatchCancelResponseDto>> cancel(
            @PathVariable UUID dispatchId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-UUID") UUID passengerId,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role") String role
    ) {
        DispatchCancelCommand command =
                DispatchCancelCommandMapper.toCommand(passengerId, role, dispatchId);

        DispatchCancelResult result =
                dispatchCancelService.cancel(command);

        DispatchCancelResponseDto responseDto =
                DispatchCancelResponseMapper.toCancelResponse(result);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }
}
