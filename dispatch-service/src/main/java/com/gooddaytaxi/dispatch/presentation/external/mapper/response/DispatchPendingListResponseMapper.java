package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.query.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchPendingListResponseDto;

import java.util.List;

public class DispatchPendingListResponseMapper {

    public static DispatchPendingListResponseDto toDispatchPendingResponse(DispatchPendingListResult result) {
        return DispatchPendingListResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .pickupAddress(result.getPickupAddress())
                .destinationAddress(result.getDestinationAddress())
                .dispatchStatus(result.getDispatchStatus().name())
                .requestCreatedAt(result.getRequestCreatedAt())
                .build();
    }

    public static List<DispatchPendingListResponseDto> toDtoList(List<DispatchPendingListResult> results) {
        return results.stream()
                .map(DispatchPendingListResponseMapper::toDispatchPendingResponse)
                .toList();
    }
}
