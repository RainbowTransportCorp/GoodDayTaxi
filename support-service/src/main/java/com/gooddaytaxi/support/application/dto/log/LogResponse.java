package com.gooddaytaxi.support.application.dto.log;

import com.gooddaytaxi.support.domain.log.model.Log;
import com.gooddaytaxi.support.domain.log.model.LogType;

import java.time.LocalDateTime;
import java.util.UUID;

public record LogResponse(
        UUID logId,
        LogType logType,
        String logMessage,
        UUID notificationId,
        LocalDateTime occurredAt
) {
    public static LogResponse from(Log l) {
        return new LogResponse(
                l.getId(),
                l.getLogType(),
                l.getLogMessage(),
                l.getNotificationId(),
                l.getOccurredAt()
        );
    }
}
