package com.gooddaytaxi.support.adapter.out.external.slack;

import com.gooddaytaxi.support.adapter.out.external.slack.dto.SlackMessageAPIRes;
import com.gooddaytaxi.support.adapter.out.external.slack.dto.SlackMessageAlertReq;
import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserInfo;
import com.gooddaytaxi.support.adapter.out.messaging.config.RabbitMQConfig;
import com.gooddaytaxi.support.application.port.out.external.NotificationAlertExternalPort;
import com.gooddaytaxi.support.application.port.out.internal.account.AccountDomainCommunicationPort;
import com.gooddaytaxi.support.application.port.out.messaging.QueuePushMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationAlertSlackAdapter implements NotificationAlertExternalPort {

    private final AccountDomainCommunicationPort accountDomainCommunicationPort;
    private final SlackFeignClient slackFeignClient;

    @RabbitListener(queues = RabbitMQConfig.DISPATCH_QUEUE)
    @Override
    public void sendCallRequest(QueuePushMessage message) {
        log.info("[Slack] Receive QueuePushMessage. title={}, body={}, receivers={}",
                message.title(), message.body(), message.receivers());

        // 메시지 정보
        String title = message.title();
        String body = message.body();
        String formattedMessage = "*%s*\n%s".formatted(title, body);

        log.info("‼️‼️‼️‼️ Account FeignClient 사용 전");
        // 메시지 수신자 Slack ID 추출
        List<UUID> receivers = message.receivers();
        UserInfo driver = accountDomainCommunicationPort.getUserInfo(receivers.get(0));

        log.info("‼️‼️‼️‼️ Account FeignClient 사용 후");
        List<String> slackTargets = new ArrayList<>();
        slackTargets.add(driver.slackUserId());
            // Driver
        String driverSlackId = slackTargets.get(0);

        log.info("‼️‼️‼️‼️ Slack ID 가져오기 {}", driverSlackId);

        // Slack 알림 Push
//        for (String target : slackTargets) {
            SlackMessageAlertReq request = SlackMessageAlertReq.builder()
                    .channel(driverSlackId)  // target : Public Channel(ex. #general, C017L34ABC1 등), Private Channel(ex. C0ABCDEF01 등), DM(사용자 Slack User ID), Multi-person DM(ex. G12345678 등)
                    .text(formattedMessage)
                    .mrkdwn(true)            // Slack Markdown 사용
                    .build();
            try {
                SlackMessageAPIRes response = slackFeignClient.postMessage(request);
                if (!response.ok()) {
                    log.error("[Slack] API Error: target={}, error={}", driverSlackId, response.error());
                } else {
                    log.info("[Slack] Message Sent. target={}, ts={}", driverSlackId, response.ts());
                }
            } catch (Exception e) {
                log.error("[Slack] Failed to send Slack message. target={}, title={}", driverSlackId, title, e);
            }
//        }
    }
}
