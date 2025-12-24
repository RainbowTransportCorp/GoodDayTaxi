package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.application.event.SystemNotifier;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchTimeoutPayload(
        UUID notificationOriginId,   // dispatchId
        UUID notifierId,             // SYSTEM or ADMIN UUID
        UUID dispatchId,
        UUID passengerId,
        String message,
        LocalDateTime timeoutAt
) {

    // 자동 타임아웃
    public static DispatchTimeoutPayload auto(
            UUID dispatchId,
            UUID passengerId,
            LocalDateTime timeoutAt
    ) {
        return new DispatchTimeoutPayload(
                dispatchId,
                SystemNotifier.SYSTEM_ID,
                dispatchId,
                passengerId,
                "배차 시간이 초과되었습니다.",
                timeoutAt
        );
    }
}
