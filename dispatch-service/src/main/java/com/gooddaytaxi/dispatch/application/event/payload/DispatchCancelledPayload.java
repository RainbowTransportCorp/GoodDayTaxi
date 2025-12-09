package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchCancelledPayload(
        UUID dispatchId,
        UUID passengerId,
        String cancelledBy,          // PASSENGER or SYSTEM
        LocalDateTime cancelledAt
) {

    // 승객 취소
    public static DispatchCancelledPayload fromPassenger(Dispatch dispatch) {
        return new DispatchCancelledPayload(
                dispatch.getDispatchId(),
                dispatch.getPassengerId(),
                "PASSENGER",
                LocalDateTime.now()
        );
    }

    // 시스템 자동 취소 (타임아웃 등)
    public static DispatchCancelledPayload fromSystem(Dispatch dispatch) {
        return new DispatchCancelledPayload(
                dispatch.getDispatchId(),
                dispatch.getPassengerId(),
                "SYSTEM",
                LocalDateTime.now()
        );
    }
}

