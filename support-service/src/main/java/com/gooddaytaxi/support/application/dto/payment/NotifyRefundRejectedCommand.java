package com.gooddaytaxi.support.application.dto.payment;

import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class NotifyRefundRejectedCommand extends Command {
    private final UUID refundRequestId;
    private final UUID adminId;
    //    private final UUID dispatchId;
    private final UUID tripId;
    private final UUID paymentId;
    private final UUID passengerId;
    private final String rejectReason;
    private final LocalDateTime rejectedAt;

    private NotifyRefundRejectedCommand(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID passengerId,
            String rejectReason,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        super(notificationOriginId, notifierId, null, metadata);
        this.refundRequestId = notificationOriginId;
        this.adminId = notifierId;
//        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.paymentId = paymentId;
        this.passengerId = passengerId;
        this.rejectReason = rejectReason;
        this.rejectedAt = rejectedAt;
    }
    public static NotifyRefundRejectedCommand create(
            UUID notificationOriginId, UUID notifierId,
//            UUID dispatchId,
            UUID tripId,
            UUID paymentId,
            UUID passengerId,
            String rejectReason,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        return new NotifyRefundRejectedCommand(notificationOriginId, notifierId, tripId, paymentId, passengerId, rejectReason, rejectedAt, metadata);
    }
}

