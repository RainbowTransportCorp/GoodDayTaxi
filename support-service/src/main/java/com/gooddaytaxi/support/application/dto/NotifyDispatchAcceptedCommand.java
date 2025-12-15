package com.gooddaytaxi.support.application.dto;

import com.gooddaytaxi.support.application.Metadata;
import lombok.Getter;

import java.util.UUID;

/**
 * 수락된 배차 정보 알림 Command
 * - DISPATCH_ACCEPTED 이벤트 처리
 */
@Getter
public class NotifyDispatchAcceptedCommand extends Command{
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;

    private NotifyDispatchAcceptedCommand(
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
    public static NotifyDispatchAcceptedCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message,
            Metadata metadata
    ) {
        return new NotifyDispatchAcceptedCommand(notificationOriginId, notifierId, driverId, passengerId, pickupAddress, destinationAddress, message, metadata);
    }
}
