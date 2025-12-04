package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.result.TripCreateResult;
import com.gooddaytaxi.trip.application.service.TripService;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.presentation.dto.request.CreateTripRequest;
import com.gooddaytaxi.trip.presentation.dto.response.CreateTripResponse;
import com.gooddaytaxi.trip.presentation.mapper.command.TripCreateRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.TripCreateResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final TripCreateRequestMapper tripCreateRequestMapper;
    private final TripCreateResponseMapper tripCreateResponseMapper;


    @PostMapping
    public ResponseEntity<ApiResponse<CreateTripResponse>> createTrip(@Valid @RequestBody CreateTripRequest request) {

        // 1. Request → Command
        TripCreateCommand command = tripCreateRequestMapper.toCommand(request);

        // 2. Service 호출
        TripCreateResult result = tripService.createTrip(command);

        // 3. Result → Response DTO
        CreateTripResponse response = tripCreateResponseMapper.toResponse(result);

        // 4. ApiResponse 래핑 후 201 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

}





