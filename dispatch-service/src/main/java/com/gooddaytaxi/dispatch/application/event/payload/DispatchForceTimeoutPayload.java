package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchForceTimeoutPayload(
    UUID notificationOriginId,           // dispatchId
    UUID notifierId,                     // SYSTEM or ADMIN UUID
    UUID dispatchId,
    UUID driverId,                       // 기사 알림 대상
    DispatchStatus previousStatus,       // REQUESTED/ASSIGNING/ASSIGNED/ACCEPTED/TRIP_REQUEST
    boolean tripRequestMayHaveBeenSent,  // previousStatus 기반 힌트
    String reason,                       // 운영 사유(옵션)
    String message,                      // 기사에게 전달할 메시지
    LocalDateTime forceTimeoutAt
) {

    public static DispatchForceTimeoutPayload adminForce(
        UUID dispatchId,
        UUID driverId,
        DispatchStatus previousStatus,
        UUID adminId,
        String reason,
        LocalDateTime forceTimeoutAt
    ) {
        boolean maySent = previousStatus == DispatchStatus.ACCEPTED
            || previousStatus == DispatchStatus.TRIP_REQUEST;

        String msg = (reason != null && !reason.isBlank())
            ? "해당 배차는 운영자에 의해 중단되었습니다. 사유: " + reason + " 운행 시작 버튼을 누를 수 없습니다."
            : "해당 배차는 운영자에 의해 중단되었습니다. 운행 시작 버튼을 누를 수 없습니다.";

        return new DispatchForceTimeoutPayload(
            dispatchId,   // notificationOriginId
            adminId,
            dispatchId,
            driverId,
            previousStatus,
            maySent,
            reason,
            msg,
            forceTimeoutAt
        );
    }
}
