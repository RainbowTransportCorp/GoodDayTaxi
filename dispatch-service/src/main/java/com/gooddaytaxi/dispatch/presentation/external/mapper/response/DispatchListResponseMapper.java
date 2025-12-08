package com.gooddaytaxi.dispatch.presentation.external.mapper.response;


import com.gooddaytaxi.dispatch.application.result.DispatchSummaryResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchListResponseDto;

import java.util.List;

public class DispatchListResponseMapper {

    public static DispatchListResponseDto toDispatchListResponse(DispatchSummaryResult summary) {
        return DispatchListResponseDto.builder()
                .dispatchId(summary.getDispatchId())
                .pickupAddress(summary.getPickupAddress())
                .destinationAddress(summary.getDestinationAddress())
                .dispatchStatus(summary.getDispatchStatus().name())
                .driverId(summary.getDriverId())
                .requestCreatedAt(summary.getCreatedAt())
                .build();
    }

    public static List<DispatchListResponseDto> toDispatchListResponseList(List<DispatchSummaryResult> summaries) {
        return summaries.stream()
                .map(DispatchListResponseMapper::toDispatchListResponse)
                .toList();
    }
}

