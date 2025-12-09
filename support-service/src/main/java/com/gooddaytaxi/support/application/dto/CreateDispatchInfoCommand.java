package com.gooddaytaxi.support.application.dto;

import lombok.Getter;

import java.util.UUID;

/**
 * CallRequest Command - 콜 요청하는 Event
 */
@Getter
public class CreateDispatchInfoCommand extends Command{
//    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String pickupAddress;
    private final String destinationAddress;

    private CreateDispatchInfoCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message
    ) {
        super(notificationOriginId, notifierId, message);
//        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
    }
    public static CreateDispatchInfoCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message
    ) {
        return new CreateDispatchInfoCommand(notificationOriginId, notifierId, driverId, passengerId, pickupAddress, destinationAddress, message);
    }
}
