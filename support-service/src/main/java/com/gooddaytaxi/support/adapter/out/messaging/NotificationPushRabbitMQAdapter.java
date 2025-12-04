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

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationPushRabbitMQAdapter implements NotificationPushMessagingPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(List<UUID> receivers, String title, String body) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                new PushMessage(receivers, title, body)
        );
    }
}
