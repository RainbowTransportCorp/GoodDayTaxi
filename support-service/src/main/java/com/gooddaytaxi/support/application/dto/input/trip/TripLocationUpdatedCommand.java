package com.gooddaytaxi.support.application.dto.input.trip;

import com.gooddaytaxi.support.application.dto.input.Command;
import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 운행 중 목적지 변경 알림 Command
 * - TRIP_LOCATION_UPDATED 이벤트 처리
 */
@Getter
public class TripLocationUpdatedCommand extends Command {
    private final UUID tripId;
    private final UUID dispatchId;
    private final UUID driverId;
    private final String currentAddress;
    private final String region;
    private final String previousRegion;
    private final Long sequence;
    private final LocalDateTime locationTime;

    private TripLocationUpdatedCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            String currentAddress, String region,
            String previousRegion,
            Long sequence,
            LocalDateTime locationTime,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.tripId = notificationOriginId;
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.currentAddress = currentAddress;
        this.region = region;
        this.previousRegion = previousRegion;
        this.sequence = sequence;
        this.locationTime = locationTime;
    }

    public static TripLocationUpdatedCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId,
            UUID driverId,
            String currentAddress, String region,
            String previousRegion,
            Long sequence,
            LocalDateTime locationTime,
            Metadata metadata
    ) {
        return new TripLocationUpdatedCommand(notificationOriginId, notifierId, dispatchId, driverId, currentAddress, region, previousRegion, sequence, locationTime, metadata);
    }

    public Notification toEntity(NotificationType notificationType) {
        return Notification.create(this.getNotificationOriginId(), this.getNotifierId(), notificationType, this.getMessage());
    }

}
