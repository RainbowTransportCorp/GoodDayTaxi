package com.gooddaytaxi.dispatch.application.event.payload;

import java.util.UUID;

public record DispatchTripReadyErrorPayload(
    UUID notificationOriginId,
    UUID dispatchId,
    UUID passengerId,
    String sourceNotificationType,
    String logType,
    String logMessage
) {

    public static DispatchTripReadyErrorPayload tripReadyTimeout(
        UUID dispatchId,
        UUID passengerId,
        long elapsedSeconds
    ) {
        return new DispatchTripReadyErrorPayload(
            dispatchId,                 // notificationOriginId
            dispatchId,
            passengerId,
            "TRIP_CREATE_REQUEST",
            "DISPATCH_ERROR",
            "TRIP_CREATE_REQUEST 이후 "
                + elapsedSeconds
                + "초 동안 Trip READY 이벤트가 확인되지 않음"
        );
    }
}
