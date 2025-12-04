package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.result.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchAcceptResponseDto;

public class DispatchAcceptResponseMapper {
    public static DispatchAcceptResponseDto toResponse(DispatchAcceptResult result) {
        return DispatchAcceptResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .driverId(result.getDriverId())
                .dispatchStatus(result.getDispatchStatus().name())
                .acceptedAt(result.getAcceptedAt())
                .build();
    }
}
