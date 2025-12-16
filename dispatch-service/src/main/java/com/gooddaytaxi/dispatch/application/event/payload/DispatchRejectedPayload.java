package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchRejectedPayload(
        UUID notificationOriginId,   // dispatchId
        UUID notifierId,             // driverId
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String message,
        LocalDateTime rejectedAt
) {

    public static DispatchRejectedPayload from(Dispatch dispatch) {
        return new DispatchRejectedPayload(
                dispatch.getDispatchId(),
                dispatch.getDriverId(),
                dispatch.getDispatchId(),
                dispatch.getDriverId(),
                dispatch.getPassengerId(),
                "기사가 콜 요청을 거절했습니다.",
                LocalDateTime.now()
        );
    }
}
