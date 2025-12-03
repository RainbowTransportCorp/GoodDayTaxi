package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.result.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.application.service.DispatchService;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchPendingListResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchPendingListResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispatches/driver")
public class DriverDispatchController {

    private final DispatchService dispatchService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<DispatchPendingListResponseDto>>> getPendingDispatches(
            //            ,@RequestHeader(value = "x-user-uuid", required = false) UUID userId
    ) {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000011");
        List<DispatchPendingListResult> dispatchPendingListResults =
                dispatchService.getDriverPendingDispatch(userId);

        List<DispatchPendingListResponseDto> responseDto =
                DispatchPendingListResponseMapper.toDtoList(dispatchPendingListResults);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}
