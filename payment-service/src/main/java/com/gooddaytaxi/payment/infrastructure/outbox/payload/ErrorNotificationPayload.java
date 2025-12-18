package com.gooddaytaxi.payment.infrastructure.outbox.payload;

public record ErrorNotificationPayload(
        String notificationOriginId,
        String notifierId,

        String tripId,
        String paymentId,
        String driverId,
        String passengerId,

        String sourceNotificationType,
        String logType,
        String logMessage
) {
}
