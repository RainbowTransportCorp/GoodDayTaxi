package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchRejectResponseDto;

public class DispatchRejectResponseMapper {

    public static DispatchRejectResponseDto toResponse(DispatchRejectResult result) {
        return DispatchRejectResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .driverId(result.getDriverId())
                .dispatchStatus(result.getDispatchStatus().name())
                .rejectedAt(result.getRejectedAt())
                .build();
    }
}

