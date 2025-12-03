package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.result.DispatchPendingListResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchPendingListResponseDto;

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
}
