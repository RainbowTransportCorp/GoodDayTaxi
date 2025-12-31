package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.query.DispatchDetailResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchDetailResponseDto;

public class DispatchDetailResponseMapper {

    public static DispatchDetailResponseDto toDispatchDetailResponse(DispatchDetailResult result) {
        return DispatchDetailResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .passengerId(result.getPassengerId())
                .driverId(result.getDriverId())
                .pickupAddress(result.getPickupAddress())
                .destinationAddress(result.getDestinationAddress())
                .dispatchStatus(result.getDispatchStatus().name())
                .requestCreatedAt(result.getRequestCreatedAt())
                .assignedAt(result.getAssignedAt())
                .acceptedAt(result.getAcceptedAt())
                .canceledAt(result.getCanceledAt())
                .timeoutAt(result.getTimeoutAt())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}
