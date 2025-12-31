package com.gooddaytaxi.support.application.port.out.messaging;

import com.gooddaytaxi.support.application.port.out.dto.QueuePushMessage;

/* 알림 Push를 위한 Messaging Port
*
*/
public interface NotificationPushMessagingPort {
    void push(QueuePushMessage queuePushMessage, String Key);
}