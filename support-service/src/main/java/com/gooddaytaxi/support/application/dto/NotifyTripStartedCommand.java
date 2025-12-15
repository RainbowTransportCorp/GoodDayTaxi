package com.gooddaytaxi.support.application.dto;

import com.gooddaytaxi.support.application.Metadata;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운행 시작 알림 Command
 * - TRIP_STARTED 이벤트 처리
 */
@Getter
public class NotifyTripStartedCommand extends Command {
    private final UUID tripId;
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final LocalDateTime startTime;

    private NotifyTripStartedCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            LocalDateTime startTime,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.tripId = notificationOriginId;
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
        this.startTime = startTime;
    }
    public static NotifyTripStartedCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            LocalDateTime startTime,
            Metadata metadata
    ) {
        return new NotifyTripStartedCommand(notificationOriginId, notifierId, dispatchId, driverId, passengerId, pickupAddress, destinationAddress, startTime, metadata);
    }
}