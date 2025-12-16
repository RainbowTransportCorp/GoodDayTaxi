package com.gooddaytaxi.support.application.dto.payment;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class NotifyRefundSettlementCreatedCommand extends Command {
    private final UUID adminId;
    //    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID driverId;
    private final String method;
    private final Long amount;
    private final String reason;
    private final LocalDateTime approvedAt;

    private NotifyRefundSettlementCreatedCommand(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID driverId,
            String method,
            Long amount,
            String reason,
            LocalDateTime approvedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.paymentId = paymentId; // notificationOriginId
        this.adminId = notifierId;
//        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.driverId = driverId;
        this.method = method;
        this.amount = amount;
        this.reason = reason;
        this.approvedAt = approvedAt;
    }
    public static NotifyRefundSettlementCreatedCommand create(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID driverId,
            String method,
            Long amount,
            String reason,
            LocalDateTime approvedAt,
            Metadata metadata
    ) {
        return new NotifyRefundSettlementCreatedCommand(notificationOriginId, notifierId, tripId, paymentId, driverId, method, amount, reason, approvedAt, metadata);
    }
}


