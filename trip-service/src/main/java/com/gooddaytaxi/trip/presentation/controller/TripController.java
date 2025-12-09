package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.EndTripCommand;
import com.gooddaytaxi.trip.application.command.StartTripCommand;
import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.result.*;
import com.gooddaytaxi.trip.application.service.TripService;

import com.gooddaytaxi.trip.presentation.dto.request.CreateTripRequest;
import com.gooddaytaxi.trip.presentation.dto.request.EndTripRequest;
import com.gooddaytaxi.trip.presentation.dto.response.*;
import com.gooddaytaxi.trip.presentation.mapper.command.EndTripRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.command.TripCreateRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.*;
import jakarta.validation.Valid;

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
    private final TripStartResponseMapper tripStartResponseMapper;
    private final EndTripRequestMapper endTripRequestMapper;
    private final TripEndResponseMapper tripEndResponseMapper;
    private final PassengerTripHistoryResponseMapper passengerTripHistoryResponseMapper;
    private final DriverTripHistoryResponseMapper driverTripHistoryResponseMapper;



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

    @PutMapping("/{tripId}/start")
    public ResponseEntity<ApiResponse<TripStartResponse>> startTrip(
            @PathVariable("tripId") UUID tripId
    ) {
        StartTripCommand command = new StartTripCommand(tripId);

        TripStartResult result = tripService.startTrip(command);

        TripStartResponse response = tripStartResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tripId}/end")
    public ResponseEntity<ApiResponse<TripEndResponse>> endTrip(
            @PathVariable UUID tripId,
            @Valid @RequestBody EndTripRequest request
    ) {
        EndTripCommand command = endTripRequestMapper.toCommand(request);

        TripEndResult result = tripService.endTrip(tripId, command);

        TripEndResponse response = tripEndResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/passengers/{passengerId}")
    public ResponseEntity<ApiResponse<PassengerTripHistoryResponse>> getPassengerTripHistory(
            @PathVariable UUID passengerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PassengerTripHistoryResult result =
                tripService.getPassengerTripHistory(passengerId, page, size);

        PassengerTripHistoryResponse response =
                passengerTripHistoryResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<ApiResponse<DriverTripHistoryResponse>> getDriverTripHistory(
            @PathVariable UUID driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        DriverTripHistoryResult result =
                tripService.getDriverTripHistory(driverId, page, size);

        DriverTripHistoryResponse response =
                driverTripHistoryResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }





}





