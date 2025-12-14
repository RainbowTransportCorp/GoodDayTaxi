package com.gooddaytaxi.support.application.port.out.messaging;

/* 알림 Push를 위한 Messaging Port
*
*/
public interface NotificationPushMessagingPort {
    void push(QueuePushMessage queuePushMessage, String Key);
}