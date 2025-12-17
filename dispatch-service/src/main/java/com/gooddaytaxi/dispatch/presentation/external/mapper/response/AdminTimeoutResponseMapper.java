package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.usecase.timeout.AdminForceTimeoutResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.AdminForceTimeoutResponseDto;

public class AdminTimeoutResponseMapper {
    public static AdminForceTimeoutResponseDto toResponse(AdminForceTimeoutResult result) {
        return AdminForceTimeoutResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .status(result.getStatus())
                .timeoutAt(result.getTimeoutAt())
                .build();
    }
}
