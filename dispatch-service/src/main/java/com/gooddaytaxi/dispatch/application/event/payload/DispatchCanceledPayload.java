package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchCanceledPayload(
        UUID dispatchId,
        UUID passengerId,
        String cancelledBy,          // PASSENGER or SYSTEM
        LocalDateTime cancelledAt
) {

    // 승객 취소
    public static DispatchCanceledPayload fromPassenger(Dispatch dispatch) {
        return new DispatchCanceledPayload(
                dispatch.getDispatchId(),
                dispatch.getPassengerId(),
                "PASSENGER",
                LocalDateTime.now()
        );
    }

    // 시스템 자동 취소 (타임아웃 등)
    public static DispatchCanceledPayload fromSystem(Dispatch dispatch) {
        return new DispatchCanceledPayload(
                dispatch.getDispatchId(),
                dispatch.getPassengerId(),
                "SYSTEM",
                LocalDateTime.now()
        );
    }
}

