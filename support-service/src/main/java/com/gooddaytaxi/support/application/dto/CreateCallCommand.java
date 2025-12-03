package com.gooddaytaxi.support.application.dto;

import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * CallRequest Command - 콜 요청하는 Event
 */
@Getter
public class CreateCallCommand extends Command{
    private UUID dispatchId;
    private UUID driverId;
    private UUID passengerId;
    private String pickupAddress;
    private String destinationAddress;

    private CreateCallCommand(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId, UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message
    ) {
        super(notificationOriginId, notifierId, message);
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
    }
    public static CreateCallCommand create(
            UUID notificationOriginId, UUID notifierId,
            UUID dispatchId, UUID driverId, UUID passengerId,
            String pickupAddress, String destinationAddress,
            String message
    ) {
        return new CreateCallCommand(notificationOriginId, notifierId, dispatchId, driverId, passengerId, pickupAddress, destinationAddress, message);
    }
}
