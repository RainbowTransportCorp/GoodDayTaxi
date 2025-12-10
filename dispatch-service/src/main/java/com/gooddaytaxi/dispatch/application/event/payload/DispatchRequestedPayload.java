package com.gooddaytaxi.dispatch.application.event.payload;


import java.util.UUID;

public record DispatchRequestedPayload(
        UUID notificationOriginId,
        UUID notifierId,
        UUID driverId,
        UUID passengerId,
        String pickupAddress,
        String destinationAddress,
        String message
) {
    public static DispatchRequestedPayload from(
            UUID dispatchId,
            UUID notifierId,      // 승객 (or 관리자)
            UUID driverId,
            UUID passengerId,     // 알림 메시지 구성용
            String pickupAddress,
            String destinationAddress,
            String message
    ) {
        return new DispatchRequestedPayload(
                dispatchId,
                notifierId,
                driverId,
                passengerId,
                pickupAddress,
                destinationAddress,
                message
        );
    }

}

