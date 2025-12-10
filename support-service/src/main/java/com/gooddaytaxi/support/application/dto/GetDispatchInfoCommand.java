package com.gooddaytaxi.support.application.dto;

/**
 * DispatchAccept Command - 배차 수락하는 Event
 */

import lombok.Getter;

import java.util.UUID;

/**
 * CallRequest Command - 콜 요청하는 Event
 */
@Getter
public class GetDispatchInfoCommand extends Command{
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;

    private GetDispatchInfoCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message
    ) {
        super(notificationOriginId, notifierId, message);
        this.dispatchId = notificationOriginId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
    }
    public static GetDispatchInfoCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message
    ) {
        return new GetDispatchInfoCommand(notificationOriginId, notifierId, driverId, passengerId, pickupAddress, destinationAddress, message);
    }
}
