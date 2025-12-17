package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 수락된 배차 정보 알림 Command
 * - DISPATCH_ACCEPTED 이벤트 처리
 */
@Getter
public class NotifyDispatchAcceptedCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final LocalDateTime acceptedAt;

    private NotifyDispatchAcceptedCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message,
            LocalDateTime acceptedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, message, metadata);
        this.dispatchId = notificationOriginId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
        this.acceptedAt = acceptedAt;
    }
    public static NotifyDispatchAcceptedCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message,
            LocalDateTime acceptedAt,
            Metadata metadata
    ) {
        return new NotifyDispatchAcceptedCommand(notificationOriginId, notifierId, driverId, passengerId, pickupAddress, destinationAddress, message, acceptedAt, metadata);
    }
}
