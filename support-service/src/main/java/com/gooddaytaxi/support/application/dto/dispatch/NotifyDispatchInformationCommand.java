package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.util.UUID;

/**
 * 배차 정보 알림 Command
 * - DISPATCH_REQUESTED 이벤트 처리
 */
@Getter
public class NotifyDispatchInformationCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;

    private NotifyDispatchInformationCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.dispatchId = notificationOriginId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
    }
    public static NotifyDispatchInformationCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message,
            Metadata metadata
    ) {
        return new NotifyDispatchInformationCommand(notificationOriginId, notifierId, driverId, passengerId, pickupAddress, destinationAddress, message, metadata);
    }

    @Override
    public String toString() {
        return "NotifyDispatchInformationCommand(driverId=%s, passengerId=%s, message=%s, dispatchId=%s)"
                .formatted(driverId, passengerId, getMessage(), dispatchId);
    }
}
