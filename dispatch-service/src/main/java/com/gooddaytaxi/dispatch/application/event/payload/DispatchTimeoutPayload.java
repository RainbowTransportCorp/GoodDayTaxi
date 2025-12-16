package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.application.event.SystemNotifier;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchTimeoutPayload(
        UUID notificationOriginId,   // dispatchId
        UUID notifierId,             // SYSTEM
        UUID dispatchId,
        UUID passengerId,
        String message,
        LocalDateTime timeoutAt
) {

    public static DispatchTimeoutPayload from(
            UUID dispatchId,
            UUID passengerId,
            LocalDateTime timeoutAt
    ) {
        return new DispatchTimeoutPayload(
                dispatchId,
                SystemNotifier.SYSTEM_ID, // SYSTEM 전용 UUID
                dispatchId,
                passengerId,
                "배차 시간이 초과되었습니다.",
                timeoutAt
        );
    }
}
