package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.FarePolicyCreateCommand;
import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.application.service.FarePolicyService;
import com.gooddaytaxi.trip.presentation.dto.request.FarePolicyRequest;
import com.gooddaytaxi.trip.presentation.dto.response.FarePolicyResponse;
import com.gooddaytaxi.trip.presentation.mapper.command.FarePolicyRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.FarePolicyResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class TripController {

    private final FarePolicyService farePolicyService;
    private final FarePolicyRequestMapper requestMapper;
    private final FarePolicyResponseMapper responseMapper;
 @PostMapping
 public ResponseEntity<ApiResponse<FarePolicyResponse>> createPolicy(@Valid @RequestBody FarePolicyRequest request){



     FarePolicyCreateCommand command = requestMapper.toCommand(request);


     FarePolicyCreateResult result = farePolicyService.createFarePolicy(command);


     FarePolicyResponse responseDto = responseMapper.toResponse(result);


     return ResponseEntity.status(HttpStatus.CREATED)
             .body(ApiResponse.success(responseDto));
 }

}
