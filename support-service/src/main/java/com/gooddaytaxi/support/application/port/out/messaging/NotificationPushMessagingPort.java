package com.gooddaytaxi.support.application.port.out.messaging;

import java.util.List;
import java.util.UUID;

/* 알림 Push를 위한 Messaging Port
*
*/
public interface NotificationPushMessagingPort {
    void send(List<UUID> receiverIds, String title, String body);
}