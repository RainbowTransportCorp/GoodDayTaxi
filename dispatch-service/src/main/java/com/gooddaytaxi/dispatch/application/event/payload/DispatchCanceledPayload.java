package com.gooddaytaxi.dispatch.application.event.payload;

import com.gooddaytaxi.dispatch.application.event.SystemNotifier;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchCanceledPayload(
        UUID notificationOriginId,   // dispatchId
        UUID notifierId,             // driver or SYSTEM
        UUID dispatchId,
        UUID driverId,
        UUID passengerId,
        String canceledBy,          // PASSENGER | SYSTEM
        String message,
        LocalDateTime cancelledAt
) {

    // 승객 취소
    public static DispatchCanceledPayload fromPassenger(Dispatch dispatch) {
        return new DispatchCanceledPayload(
                dispatch.getDispatchId(), // 알림용 (배차에서 보냈음)
                dispatch.getDriverId(), // 알림용 (누구에게 보낼 것)
                dispatch.getDispatchId(),
                dispatch.getDriverId(),
                dispatch.getPassengerId(),
                "PASSENGER",
                "승객이 배차를 취소했습니다.",
                LocalDateTime.now()
        );
    }

    // 시스템 취소 (타임아웃 등)
    public static DispatchCanceledPayload fromSystem(Dispatch dispatch) {
        return new DispatchCanceledPayload(
                dispatch.getDispatchId(),
                SystemNotifier.SYSTEM_ID, // SYSTEM
                dispatch.getDispatchId(),
                dispatch.getDriverId(),
                dispatch.getPassengerId(),
                SystemNotifier.SYSTEM_ID.toString(), //SYSTEM
                "배차가 자동으로 취소되었습니다.",
                LocalDateTime.now()
        );
    }
}
