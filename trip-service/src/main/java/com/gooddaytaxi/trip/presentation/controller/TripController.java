package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.result.TripCreateResult;
import com.gooddaytaxi.trip.application.result.TripItem;
import com.gooddaytaxi.trip.application.result.TripListResult;
import com.gooddaytaxi.trip.application.service.TripService;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.presentation.dto.request.CreateTripRequest;
import com.gooddaytaxi.trip.presentation.dto.response.CreateTripResponse;
import com.gooddaytaxi.trip.presentation.dto.response.TripListResponse;
import com.gooddaytaxi.trip.presentation.dto.response.TripResponse;
import com.gooddaytaxi.trip.presentation.mapper.command.TripCreateRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.TripCreateResponseMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.TripDetailResponseMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.TripListResponseMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final TripCreateRequestMapper tripCreateRequestMapper;
    private final TripCreateResponseMapper tripCreateResponseMapper;
    private final TripListResponseMapper tripListResponseMapper;
    private final TripDetailResponseMapper tripDetailResponseMapper;


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

    @GetMapping
    public ResponseEntity<ApiResponse<TripListResponse>> getTrips() {

        // 1. Service → 전체 목록 조회
        TripListResult result = tripService.loadTrips();

        // 2. Result → Response DTO 변환
        TripListResponse response = tripListResponseMapper.toResponse(result);
        // 3. ApiResponse 감싸서 반환

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<ApiResponse<TripResponse>> getTripDetail(
            @PathVariable UUID tripId
    ){
        // 1. Service 호출
        TripItem result = tripService.getTripDetail(tripId);

        // 2. Result → Response DTO
        TripResponse response = tripDetailResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


}





