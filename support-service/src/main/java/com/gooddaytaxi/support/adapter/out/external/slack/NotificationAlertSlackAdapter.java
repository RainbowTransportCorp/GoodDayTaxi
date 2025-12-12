package com.gooddaytaxi.support.adapter.out.external.slack;

import com.gooddaytaxi.support.adapter.in.kafka.dto.EventMetadata;
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


/**
* 외부 시스템으로 알림을 보내는 Port를 구현한 Adapter
* */
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationAlertSlackAdapter implements NotificationAlertExternalPort {

    private final AccountDomainCommunicationPort accountDomainCommunicationPort;
    private final SlackFeignClient slackFeignClient;

    // RabbitListener 메서드 삭제 후, 직접 호출 시
    public void sendCallDirectRequest(QueuePushMessage queuePushMessage) {
        doSendSlack(queuePushMessage);
    }

    @RabbitListener(queues = RabbitMQConfig.DISPATCH_QUEUE)
    @Override
    public void sendCallRequest(QueuePushMessage queuePushMessage) {
        doSendSlack(queuePushMessage);
    }


    /**
    * Slack에 Push 알림을 보내는 메서드
    * */
    private void doSendSlack(QueuePushMessage queuePushMessage) {
        // 메타데이터
        EventMetadata emData = EventMetadata.from(queuePushMessage.metadata());
        log.debug("[Mapping] Metadata >>> EventMetaData ➡️ {}", emData);

        // 메시지
        String messageTitle = queuePushMessage.title();
        String messageBody = queuePushMessage.body();
        List<UUID> messageReceivers = queuePushMessage.receivers();
        log.debug("[Check] Slack으로 보내는 QueueMessage 데이터: title={}, body={}, receivers={}", messageTitle, messageBody, messageReceivers);

        // 메시지 포맷
        String formattedMessage = "*%s*\n%s".formatted(messageTitle, messageBody);

        // 메시지 수신자 정보 조회
        List<UserInfo> receivers = new ArrayList<>();
        try {
            log.debug("[Connect] Support Service >>> Account Feign Starting . . . ");

            for (UUID receiver:  messageReceivers) {
                if (receiver != null) {
                    UserInfo info = accountDomainCommunicationPort.getUserInfo(receiver);
                    log.debug("[Check] UserInfo from Account Feign: username={}, role={}", info.name(), info.role());
                    receivers.add(info);
                }
            }
            log.debug("[Connect] Support Service >>> Account Feign Completed! ");

        } catch (Exception e) {
            log.error("❌ [Error] Account API Feign Client Error: message={}, error={}", queuePushMessage, e.getMessage());
        }

        // 메시지 수신자 Slack ID 추출
        List<String> slackReceivers = new ArrayList<>();
        for (UserInfo receiver: receivers) {
            String slackId = receiver.slackUserId();
            log.debug("[Check] 수신자 Slack ID: username={}, slackId={}", receiver.name(), receiver.slackUserId());
            slackReceivers.add(slackId);
        }

        // Slack 알림 객체 생성
        List<SlackMessageAlertReq> requests = new ArrayList<>();
        for (String slackId: slackReceivers) {
            SlackMessageAlertReq request = SlackMessageAlertReq.builder()
                    .channel(slackId) // Public Channel(ex. #general, C017L34ABC1 등) | Private Channel(ex. C0ABCDEF01 등) | DM(사용자 Slack User ID) | Multi-person DM(ex. G12345678 등)
                    .text(formattedMessage)
                    .mrkdwn(true)
                    .build();
            requests.add(request);
        }

        // 수신자에게 Slack 알림 Push
        for (SlackMessageAlertReq request: requests) {
            try {
                SlackMessageAPIRes response = slackFeignClient.postMessage(request);
                if (!response.ok()) {
                    log.error("❌ [Error] Slack API Error: targetReceiver={}, error={}", request.channel(), response.error());
                } else {
                    log.info("[Check] Slack Message Sent. targetReceiver={}, ts={}", request.channel(), response.ts());
                }
            } catch (Exception e) {
                log.error("❌ [Error] Failed to send Slack message. target={}, title={}, error={}", request.channel(), messageTitle, e.getMessage());
            }
        }
    }

}
