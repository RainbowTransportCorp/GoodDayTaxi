package com.gooddaytaxi.support.adapter.out.messaging;

import com.gooddaytaxi.support.adapter.out.messaging.config.RabbitMQConfig;
import com.gooddaytaxi.support.adapter.out.messaging.dto.PushMessage;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/* RabbitMQ에 메시지 Push하는 Port를 구현한 Adapter
*
*/
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationPushRabbitMQAdapter implements NotificationPushMessagingPort {

    private final RabbitTemplate rabbitTemplate;
    private final NotificationPushMessagingPort notificationPushMessagingPort;

    @Override
    public void send(List<UUID> receivers, String title, String body) {
        // RabbitMQ Queue에 메시지 Push
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                new PushMessage(receivers, title, body)
        );

        // Queue에서 메시지 취득하여 Slack에 알림



    }
}
