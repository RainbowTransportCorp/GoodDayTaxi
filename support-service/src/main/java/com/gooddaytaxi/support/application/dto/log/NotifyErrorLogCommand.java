package com.gooddaytaxi.support.application.dto.log;

import com.gooddaytaxi.support.application.dto.Command;
import com.gooddaytaxi.support.application.dto.Metadata;
import lombok.Getter;

import java.util.UUID;

/**
 * 에러 탐지 알림 Command
 * - ERROR_DETECTED 이벤트 처리
 */
@Getter
public class NotifyErrorLogCommand extends Command {
    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String sourceNotificationType;
    private final String logType;

    private NotifyErrorLogCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId, UUID tripId, UUID paymentId,
            UUID driverId, UUID passengerId,
            String sourceNotificationType, String logType,
            String logMessage,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, logMessage, metadata);
        this.logType = logType;
        this.sourceNotificationType = sourceNotificationType;
        this.driverId = driverId;
        this.passengerId = passengerId;

        switch (logType) {
            case "DISPATCH_ERROR" -> {
                if (dispatchId == null) {
                    this.dispatchId = notificationOriginId;
                } else this.dispatchId = dispatchId;
                this.tripId = tripId;
                this.paymentId = paymentId;
            }
            case "TRIP_ERROR" -> {
                if (tripId == null) {
                    this.tripId = notificationOriginId;
                } else this.tripId = tripId;
                this.dispatchId = dispatchId;
                this.paymentId = paymentId;
            }
            case "PAYMENT_ERROR" -> {
                if (tripId == null) {
                    this.paymentId = notificationOriginId;
                } else this.paymentId = paymentId;
                this.dispatchId = dispatchId;
                this.tripId = tripId;
            }
            default -> {
                this.dispatchId = dispatchId;
                this.tripId = tripId;
                this.paymentId = paymentId;
            }

        }
    }
    public static NotifyErrorLogCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId, UUID tripId, UUID paymentId,
            UUID driverId, UUID passengerId,
            String sourceNotificationType, String logType,
            String logMessage,
            Metadata metadata
    ) {
        return new NotifyErrorLogCommand(notificationOriginId, notifierId, dispatchId, tripId, paymentId, driverId, passengerId, sourceNotificationType, logType, logMessage, metadata);
    }
}

