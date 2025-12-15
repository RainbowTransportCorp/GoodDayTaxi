package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.usecase.admin.AdminDispatchDetailResult;
import com.gooddaytaxi.dispatch.application.usecase.admin.AdminDispatchListResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminDispatchDetailResponseDto;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminDispatchListResponseDto;

import java.util.List;

public class AdminDispatchResponseMapper {

    private AdminDispatchResponseMapper() {
    }

    /* =======================
     * Detail
     * ======================= */
    public static AdminDispatchDetailResponseDto toDetailResponse(
            AdminDispatchDetailResult result
    ) {
        return AdminDispatchDetailResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .passengerId(result.getPassengerId())
                .driverId(result.getDriverId())
                .status(result.getStatus())
                .pickupAddress(result.getPickupAddress())
                .destinationAddress(result.getDestinationAddress())
                .reassignCount(result.getReassignCount())
                .assignedAt(result.getAssignedAt())
                .acceptedAt(result.getAcceptedAt())
                .timeoutAt(result.getTimeoutAt())
                .build();
    }

    /* =======================
     * List (단건)
     * ======================= */
    public static AdminDispatchListResponseDto toListResponse(
            AdminDispatchListResult result
    ) {
        return AdminDispatchListResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .passengerId(result.getPassengerId())
                .driverId(result.getDriverId())
                .status(result.getStatus())
                .reassignCount(result.getReassignCount())
                .requestedAt(result.getRequestedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }

    /* =======================
     * List (컬렉션)
     * ======================= */
    public static List<AdminDispatchListResponseDto> toListResponses(
            List<AdminDispatchListResult> results
    ) {
        return results.stream()
                .map(AdminDispatchResponseMapper::toListResponse)
                .toList();
    }
}