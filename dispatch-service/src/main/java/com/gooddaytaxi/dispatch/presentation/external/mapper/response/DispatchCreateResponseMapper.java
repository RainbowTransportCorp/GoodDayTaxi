package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.usecase.create.DispatchCreateResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCreateResponseDto;

public class DispatchCreateResponseMapper {

    public static DispatchCreateResponseDto toCreateResponse(DispatchCreateResult result) {
        return DispatchCreateResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .passengerId(result.getPassengerId())
                .pickupAddress(result.getPickupAddress())
                .destinationAddress(result.getDestinationAddress())
                .dispatchStatus(result.getDispatchStatus().name())
                .requestCreatedAt(result.getRequestCreatedAt())
                .createdAt(result.getCreatedAt())
                .updateAt(result.getUpdatedAt())
                .build();
    }
}