package com.gooddaytaxi.support.application.dto.trip;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운행 종료 알림 Command
 * - TRIP_ENDED 이벤트 처리
 */
@Getter
public class NotifyTripEndedCommand extends Command {
    private final UUID tripId;
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Long totalDuration;
    private final Long totalDistance;
    private final Long finalFare;

    private NotifyTripEndedCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            LocalDateTime startTime, LocalDateTime endTime,
            Long totalDuration, Long totalDistance,
            Long finalFare,
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
        this.endTime = endTime;
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.finalFare = finalFare;
    }

    public static NotifyTripEndedCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            LocalDateTime startTime, LocalDateTime endTime,
            Long totalDuration, Long totalDistance,
            Long finalFare,
            Metadata metadata
    ) {
        return new NotifyTripEndedCommand(notificationOriginId, notifierId, dispatchId, driverId, passengerId, pickupAddress, destinationAddress, startTime, endTime, totalDuration, totalDistance, finalFare, metadata);
    }
}