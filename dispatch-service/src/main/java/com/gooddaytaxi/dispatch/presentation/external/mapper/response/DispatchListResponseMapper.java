package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.result.DispatchListResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchListResponseDto;

public class DispatchListResponseMapper {

    public static DispatchListResponseDto toDispatchListResponse(DispatchListResult result) {
        return DispatchListResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .pickupAddress(result.getPickupAddress())
                .destinationAddress(result.getDestinationAddress())
                .dispatchStatus(result.getDispatchStatus().name())
                .driverId(result.getDriverId())
                .requestCreatedAt(result.getCreatedAt())
                .build();
    }

}
