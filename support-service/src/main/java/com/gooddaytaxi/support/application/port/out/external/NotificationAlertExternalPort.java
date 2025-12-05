package com.gooddaytaxi.support.application.port.out.external;

import java.util.List;
import java.util.UUID;

/* 메시지 전송 알림 External API Port
 *
 */
public interface NotificationAlertExternalPort {
    void sendCallRequest(String externalInfo, List<UUID> receiverIds, String title, String body);
}
