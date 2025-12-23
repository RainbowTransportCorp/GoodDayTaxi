package com.gooddaytaxi.trip.presentation.controller;

import com.gooddaytaxi.common.core.dto.ApiResponse;
import com.gooddaytaxi.trip.application.command.*;
import com.gooddaytaxi.trip.application.result.*;
import com.gooddaytaxi.trip.application.service.TripService;

import com.gooddaytaxi.trip.domain.model.enums.CancelReason;
import com.gooddaytaxi.trip.domain.model.enums.UserRole;
import com.gooddaytaxi.trip.presentation.dto.request.CreateTripRequest;
import com.gooddaytaxi.trip.presentation.dto.request.EndTripRequest;
import com.gooddaytaxi.trip.presentation.dto.response.*;
import com.gooddaytaxi.trip.presentation.mapper.command.EndTripRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.command.TripCreateRequestMapper;
import com.gooddaytaxi.trip.presentation.mapper.result.*;
import io.swagger.v3.oas.annotations.Parameter;
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
            @PathVariable("tripId") UUID tripId,
            @RequestHeader(value = "x-user-uuid") UUID notifierId
    ) {

        StartTripCommand command = new StartTripCommand(tripId, notifierId);

        TripStartResult result = tripService.startTrip(command);
        TripStartResponse response = tripStartResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tripId}/end")
    public ResponseEntity<ApiResponse<TripEndResponse>> endTrip(
            @PathVariable UUID tripId,
            @RequestHeader(value = "x-user-uuid", required = false) UUID notifierId,
            @Valid @RequestBody EndTripRequest request
    ) {

        EndTripCommand command = endTripRequestMapper.toCommand(request, notifierId);
        // ↑ mapper가 notifierId를 못 받는 구조면, 아래처럼 command 생성 방식으로 바꿔도 됨

        TripEndResult result = tripService.endTrip(tripId, command);
        TripEndResponse response = tripEndResponseMapper.toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PutMapping("/{tripId}/cancel")
    public ResponseEntity<ApiResponse<TripCancelResult>> cancelTrip(
            @PathVariable UUID tripId,
            @RequestHeader(value = "x-user-uuid") UUID notifierId,
            @RequestParam(defaultValue = "PASSENGER_REQUEST") String cancelReason
    ) {

        CancelReason reason = CancelReason.valueOf(cancelReason);

        CancelTripCommand command =
                new CancelTripCommand(tripId, notifierId, reason);

        TripCancelResult result = tripService.cancelTrip(command);
        return ResponseEntity.ok(ApiResponse.success(result));
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


    @PutMapping("/{tripId}/location")
    public ResponseEntity<ApiResponse<TripLocationUpdatedResponse>> updateLocation(
            @PathVariable UUID tripId,
            @RequestHeader(value = "x-user-uuid", required = false) UUID notifierId,
            @RequestParam String currentAddress,
            @RequestParam String region
    ) {

        TripLocationUpdatedResult result = tripService.publishLocationUpdate(
                new UpdateTripLocationCommand(tripId, notifierId, currentAddress, region)
        );

        TripLocationUpdatedResponse response = tripLocationUpdatedResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //-------------------프론트용 운행중 api

    @GetMapping("/passengers/active")
    public ResponseEntity<ApiResponse<TripResponse>> getPassengerActiveTrip(
        @RequestHeader(value = "X-User-UUID") UUID passengerId,
        @RequestHeader(value = "X-User-Role") String role // role은 스트링으로 받고 enum으로 변환해서 사용합니다.
    ) {
        TripItem result = tripService.getActiveTripByPassenger(passengerId, UserRole.valueOf(role));

        TripResponse response = tripDetailResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/drivers/active")
    public ResponseEntity<ApiResponse<TripResponse>> getDriverActiveTrip(
        @RequestHeader(value = "X-User-UUID") UUID driverId,
        @RequestHeader(value = "X-User-Role") String role
    ) {
        TripItem result = tripService.getActiveTripByDriver(driverId, UserRole.valueOf(role));

        TripResponse response = tripDetailResponseMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}





