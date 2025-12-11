package com.gooddaytaxi.dispatch.presentation.external.mapper.response;

import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelResult;
import com.gooddaytaxi.dispatch.presentation.external.dto.response.DispatchCancelResponseDto;

public class DispatchCancelResponseMapper {

    public static DispatchCancelResponseDto toCancelResponse(DispatchCancelResult result) {
        return DispatchCancelResponseDto.builder()
                .dispatchId(result.getDispatchId())
                .dispatchStatus(result.getDispatchStatus().name())
                .cancelledAt(result.getCancelledAt())
                .build();
    }
}
