package com.gooddaytaxi.support.application.port.out.external;

import java.util.List;

/** 메시지 전송 알림 External API Port
 *
 */
public interface NotificationAlertExternalPort {

    /**
     * 특정 Slack 대상(유저 DM or 채널)에 알림 전송
     *
     * @param slackTargets Slack User Id (Uxxx) | 채널명 (#channel-name)
     * @param title        Slack에 표시될 제목
     * @param body         Slack에 표시될 전체 텍스트 (포매팅 포함)
     */
    void sendCallRequest(List<String> slackTargets, String title, String body);
}
