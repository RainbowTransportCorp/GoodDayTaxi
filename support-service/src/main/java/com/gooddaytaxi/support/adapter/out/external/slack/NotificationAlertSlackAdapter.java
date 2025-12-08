package com.gooddaytaxi.support.adapter.out.external.slack;

import com.gooddaytaxi.support.adapter.out.external.slack.dto.SlackMessageAPIRes;
import com.gooddaytaxi.support.adapter.out.external.slack.dto.SlackMessageAlertReq;
import com.gooddaytaxi.support.adapter.out.messaging.config.RabbitMQConfig;
import com.gooddaytaxi.support.application.port.out.external.NotificationAlertExternalPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationAlertSlackAdapter implements NotificationAlertExternalPort {

    private final SlackFeignClient slackFeignClient;

    @RabbitListener(queues = RabbitMQConfig.DISPATCH_QUEUE)
    @Override
    public void sendCallRequest(List<String> slackTargets, String title, String body) {

        /** Driver Slack ID
         *
         */
        String driverSlackId = slackTargets.get(0);

        String alertText = "*%s*\n%s".formatted(title, body);

//        for (String target : slackTargets) {

            SlackMessageAlertReq request = SlackMessageAlertReq.builder()
                    .channel(driverSlackId)  // target : Public Channel(ex. #general, C017L34ABC1 등), Private Channel(ex. C0ABCDEF01 등), DM(사용자 Slack User ID), Multi-person DM(ex. G12345678 등)
                    .text(alertText)
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
