package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.*;
import com.gooddaytaxi.trip.application.result.*;
import com.gooddaytaxi.trip.application.service.TripService;

import com.gooddaytaxi.trip.domain.model.enums.CancelReason;
import com.gooddaytaxi.trip.application.validator.UserRole;
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
    private final TripLocationUpdatedResponseMapper tripLocationUpdatedResponseMapper;


    //테스트용 api, 최고관리자만 접근이 가능
    @PostMapping
    public ResponseEntity<ApiResponse<CreateTripResponse>> createTrip(
        @Valid @RequestBody CreateTripRequest request,
        @RequestHeader(value = "X-User-UUID") UUID masterId,
        @RequestHeader(value = "X-User-Role") String role
    ) {

        // 1. Request → Command
        TripCreateCommand command = tripCreateRequestMapper.toCommand(request, masterId, UserRole.valueOf(role));

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
    public ResponseEntity<ApiResponse<TripListResponse>> getTrips(
        @RequestHeader(value = "X-User-UUID") UUID userId,
        @RequestHeader(value = "X-User-Role") String role
    ) {

        // 1. Service → 전체 목록 조회
        TripListResult result = tripService.loadTrips(userId, UserRole.valueOf(role));

        // 2. Result → Response DTO 변환
        TripListResponse response = tripListResponseMapper.toResponse(result);
        // 3. ApiResponse 감싸서 반환

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<ApiResponse<TripResponse>> getTripDetail(
        @PathVariable UUID tripId,
        @RequestHeader(value = "X-User-UUID") UUID userId,
        @RequestHeader(value = "X-User-Role") String role
    ) {

        // 1. Service 호출
        TripItem result = tripService.getTripDetail(tripId, userId, UserRole.valueOf(role));

        // 2. Result → Response DTO
        TripResponse response = tripDetailResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{tripId}/start")
    public ResponseEntity<ApiResponse<TripStartResponse>> startTrip(
        @PathVariable("tripId") UUID tripId,
        @RequestHeader(value = "X-User-UUID") UUID driverId,
        @RequestHeader(value = "X-User-Role") String role
    ) {

        StartTripCommand command = new StartTripCommand(tripId, driverId, UserRole.valueOf(role));

        TripStartResult result = tripService.startTrip(command);
        TripStartResponse response = tripStartResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tripId}/end")
    public ResponseEntity<ApiResponse<TripEndResponse>> endTrip(
        @PathVariable UUID tripId,
        @RequestHeader(value = "X-User-UUID") UUID driverId,
        @RequestHeader(value = "X-User-Role") String role,
        @Valid @RequestBody EndTripRequest request
    ) {

        EndTripCommand command = endTripRequestMapper.toCommand(request, driverId, UserRole.valueOf(role));

        TripEndResult result = tripService.endTrip(tripId, command);
        TripEndResponse response = tripEndResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tripId}/cancel")
    public ResponseEntity<ApiResponse<TripCancelResult>> cancelTrip(
        @PathVariable UUID tripId,
        @RequestHeader(value = "X-User-UUID") UUID driverId,
        @RequestHeader(value = "X-User-Role") String role,
        @RequestParam(defaultValue = "PASSENGER_REQUEST") String cancelReason
    ) {

        CancelReason reason = CancelReason.valueOf(cancelReason);

        CancelTripCommand command =
            new CancelTripCommand(tripId, driverId, UserRole.valueOf(role), reason);

        TripCancelResult result = tripService.cancelTrip(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @GetMapping("/passengers") //헤더에서 uuid를 받아오므로 PathVariable 제거
    public ResponseEntity<ApiResponse<PassengerTripHistoryResponse>> getPassengerTripHistory(
        @RequestHeader(value = "X-User-UUID") UUID passengerId,
        @RequestHeader(value = "X-User-Role") String role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        PassengerTripHistoryResult result =
            tripService.getPassengerTripHistory(passengerId, UserRole.valueOf(role), page, size);

        PassengerTripHistoryResponse response =
            passengerTripHistoryResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @GetMapping("/drivers") //헤더에서 uuid를 받아오므로 PathVariable 제거
    public ResponseEntity<ApiResponse<DriverTripHistoryResponse>> getDriverTripHistory(
        @RequestHeader(value = "X-User-UUID") UUID driverId,
        @RequestHeader(value = "X-User-Role") String role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        DriverTripHistoryResult result =
            tripService.getDriverTripHistory(driverId, UserRole.valueOf(role), page, size);

        DriverTripHistoryResponse response =
            driverTripHistoryResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tripId}/location")
    public ResponseEntity<ApiResponse<TripLocationUpdatedResponse>> updateLocation(
        @PathVariable UUID tripId,
        @RequestHeader(value = "X-User-UUID") UUID driverId,
        @RequestHeader(value = "X-User-Role") String role,
        @RequestParam String currentAddress,
        @RequestParam String region
    ) {

        TripLocationUpdatedResult result = tripService.publishLocationUpdate(
            new UpdateTripLocationCommand(tripId, driverId, UserRole.valueOf(role), currentAddress, region)
        );

        TripLocationUpdatedResponse response = tripLocationUpdatedResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //-------------------프론트용 운행중 api

    @GetMapping("/passengers/active")
    public ResponseEntity<ApiResponse<TripResponse>> getPassengerActiveTrip(
        @RequestHeader("X-User-UUID") UUID passengerId,
        @RequestHeader("X-User-Role") String role
    ) {
        return tripService
            .getActiveTripByPassenger(passengerId, UserRole.valueOf(role))
            .map(trip -> {
                TripResponse response = tripDetailResponseMapper.toResponse(trip);
                return ResponseEntity.ok(ApiResponse.success(response));
            })
            .orElseGet(() -> ResponseEntity.noContent().build()); // ✅ 204
    }

    @GetMapping("/drivers/active")
    public ResponseEntity<ApiResponse<TripResponse>> getDriverActiveTrip(
        @RequestHeader("X-User-UUID") UUID driverId,
        @RequestHeader("X-User-Role") String role
    ) {
        return tripService
            .getActiveTripByDriver(driverId, UserRole.valueOf(role))
            .map(trip -> {
                TripResponse response = tripDetailResponseMapper.toResponse(trip);
                return ResponseEntity.ok(ApiResponse.success(response));
            })
            .orElseGet(() -> ResponseEntity.noContent().build()); // ✅ 204
    }
}





