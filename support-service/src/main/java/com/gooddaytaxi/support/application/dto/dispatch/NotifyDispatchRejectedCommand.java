package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배차 거절 알림 Command
 * - DISPATCH_REJECTED 이벤트 처리
 */
@Getter
public class NotifyDispatchRejectedCommand extends Command {
    private final UUID dispatchId;
    private final UUID driverId;
    private final UUID passengerId;
    private final LocalDateTime rejectedAt;

    private NotifyDispatchRejectedCommand(
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        super(dispatchId, null, null, metadata);
        this.dispatchId = dispatchId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.rejectedAt = rejectedAt;
    }
    public static NotifyDispatchRejectedCommand create(
            UUID dispatchId,
            UUID driverId,
            UUID passengerId,
            LocalDateTime rejectedAt,
            Metadata metadata
    ) {
        return new NotifyDispatchRejectedCommand(dispatchId, driverId, passengerId, rejectedAt, metadata);
    }
}