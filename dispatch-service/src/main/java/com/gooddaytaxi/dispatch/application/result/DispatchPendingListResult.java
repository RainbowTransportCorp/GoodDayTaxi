package com.gooddaytaxi.dispatch.application.result;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DispatchPendingListResult {
    private final String dispatchId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final DispatchStatus dispatchStatus;
    private final String requestCreatedAt;
}
