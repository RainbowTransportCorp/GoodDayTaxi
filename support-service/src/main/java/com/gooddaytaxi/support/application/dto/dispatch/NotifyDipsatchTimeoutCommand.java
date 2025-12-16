package com.gooddaytaxi.support.application.dto.dispatch;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.Command;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배차 시간 초과 정보 알림 Command
 * - DISPATCH_TIMEOUT 이벤트 처리
 */
@Getter
public class NotifyDipsatchTimeoutCommand extends Command {
    private final UUID dispatchId;
    private final UUID passengerId;
    private final LocalDateTime timeoutAt;

    private NotifyDipsatchTimeoutCommand(
            UUID dispatchId,
            UUID passengerId,
            LocalDateTime timeoutAt,
            Metadata metadata
    ) {
        super(dispatchId, null, null, metadata);
        this.dispatchId = dispatchId;
        this.passengerId = passengerId;
        this.timeoutAt = timeoutAt;
    }
    public static NotifyDipsatchTimeoutCommand create(
            UUID dispatchId,
            UUID passengerId,
            LocalDateTime timeoutAt,
            Metadata metadata
    ) {
        return new NotifyDipsatchTimeoutCommand(dispatchId, passengerId, timeoutAt, metadata);
    }
}
