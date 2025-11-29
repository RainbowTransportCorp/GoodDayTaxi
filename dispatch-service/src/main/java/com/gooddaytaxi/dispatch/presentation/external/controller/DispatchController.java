package com.gooddaytaxi.dispatch.presentation.external.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.dispatch.application.commend.DispatchCreateCommand;
import com.gooddaytaxi.dispatch.application.result.DispatchCreateResult;
import com.gooddaytaxi.dispatch.application.service.DispatchService;
import com.gooddaytaxi.dispatch.presentation.external.dto.request.DispatchCreateRequestDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCreateResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.mapper.command.DispatchCreateCommandMapper;
import com.gooddaytaxi.dispatch.presentation.external.mapper.response.DispatchCreateResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispatches")
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<ApiResponse<DispatchCreateResponseDto>> create(
            @RequestBody DispatchCreateRequestDto requestDto
//            ,@RequestHeader(value = "x-user-id", required = false) Long userId
            ) {
        Long userId = 1234L;
        DispatchCreateCommand createCommand = DispatchCreateCommandMapper.toCommand(requestDto);
        DispatchCreateResult createResult = dispatchService.create(userId, createCommand);
        DispatchCreateResponseDto responseDto = DispatchCreateResponseMapper.toCreateResponse(createResult);

        return  ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }
}