package com.gooddaytaxi.support.application.port.out.external;

import com.gooddaytaxi.support.application.port.out.messaging.QueuePushMessage;

/** 메시지 전송 알림 External API Port
 *
 */
public interface NotificationAlertExternalPort {

    /**
     * 특정 Slack 대상(유저 DM or 채널)에 알림 전송 (RabbitListener 호출)
     *
     * @param queuePushMessage
     *  - slackTargets  Slack User Id (Uxxx) | 채널명 (#channel-name)
     *  - title         Slack에 표시될 제목
     *  - body          Slack에 표시될 전체 텍스트 (포매팅 포함)
     */
    void sendFromDispatch(QueuePushMessage queuePushMessage);
    void sendFromTrip(QueuePushMessage queuePushMessage);
    void sendFromPayment(QueuePushMessage queuePushMessage);

    /**
    * 직접 호출
    */
    void sendDirectRequest(QueuePushMessage queuePushMessage);
}
