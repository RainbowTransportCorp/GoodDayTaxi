package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.FarePolicyCreateCommand;
import com.gooddaytaxi.trip.application.result.FarePolicyCreateResult;
import com.gooddaytaxi.trip.application.result.FarePolicyItem;
import com.gooddaytaxi.trip.application.result.FarePolicyListResult;
import com.gooddaytaxi.trip.application.service.FarePolicyService;
import com.gooddaytaxi.trip.presentation.dto.request.CreateFarePolicyRequest;
import com.gooddaytaxi.trip.presentation.dto.response.CreateFarePolicyResponse;
import com.gooddaytaxi.trip.presentation.dto.response.FarePolicyResponse;
import com.gooddaytaxi.trip.presentation.mapper.command.FarePolicyRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.FarePolicyListResponseMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.FarePolicyResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class FarePolicyController {

    private final FarePolicyService farePolicyService;
    private final FarePolicyRequestMapper requestMapper;
    private final FarePolicyResponseMapper responseMapper;
    private final FarePolicyListResponseMapper responseListMapper;
 @PostMapping
 public ResponseEntity<ApiResponse<CreateFarePolicyResponse>> createPolicy(@Valid @RequestBody CreateFarePolicyRequest request){

     FarePolicyCreateCommand command = requestMapper.toCommand(request);

     FarePolicyCreateResult result = farePolicyService.createFarePolicy(command);

     CreateFarePolicyResponse responseDto = responseMapper.toResponse(result);

     return ResponseEntity.status(HttpStatus.CREATED)
             .body(ApiResponse.success(responseDto));
 }

 @GetMapping
    public ResponseEntity<ApiResponse<List<FarePolicyResponse>>> getAllPolicies(){

     FarePolicyListResult result = farePolicyService.getAllPolicies();
     List<FarePolicyResponse> responses = responseListMapper.toListResponse(result);


     return ResponseEntity
             .ok(ApiResponse.success(responses));
 }

 @GetMapping("/{policyId}")
 public ResponseEntity<ApiResponse<FarePolicyResponse>> getPolicy(@PathVariable UUID policyId){

     FarePolicyItem result = farePolicyService.getPolicy(policyId);
     FarePolicyResponse response = responseMapper.toResponse(result);

     return ResponseEntity.ok(ApiResponse.success(response));
 }
}

