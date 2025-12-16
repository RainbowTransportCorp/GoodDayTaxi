package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배차 취소 알림 Command
 * - DISPATCH_CANCELLED 이벤트 처리
 */
@Getter
public class NotifyDispatchCancelledCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final String cancelledBy;
    private final LocalDateTime cancelledAt;

    private NotifyDispatchCancelledCommand(
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String cancelledBy,
            LocalDateTime cancelledAt,
            Metadata metadata
    ) {
        super(dispatchId, null, null, metadata);
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = cancelledAt;
    }
    public static NotifyDispatchCancelledCommand create(
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            String cancelledBy,
            LocalDateTime cancelledAt,
            Metadata metadata
    ) {
        return new NotifyDispatchCancelledCommand(dispatchId, driverId, passengerId, cancelledBy, cancelledAt, metadata);
    }
}