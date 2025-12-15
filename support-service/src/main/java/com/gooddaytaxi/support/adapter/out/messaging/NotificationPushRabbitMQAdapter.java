package com.gooddaytaxi.support.adapter.out.messaging;

import com.gooddaytaxi.support.adapter.out.messaging.config.RabbitMQConfig;
import com.gooddaytaxi.support.application.port.out.messaging.QueuePushMessage;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
* RabbitMQ에 메시지 Push하는 Port를 구현한 Adapter
*/
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationPushRabbitMQAdapter implements NotificationPushMessagingPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void push(QueuePushMessage queuePushMessage, String routingKey) {
        // RabbitMQ: 메시지 Push
        String key = "";

        try {
            switch (routingKey) {
                case "DISPATCH" -> key = RabbitMQConfig.DISPATCH_ROUTING_KEY;
                case "TRIP" -> key = RabbitMQConfig.TRIP_ROUTING_KEY;
                case "PAYMENT" -> key = RabbitMQConfig.PAYMENT_ROUTING_KEY;
                default -> throw new IllegalArgumentException("Unknown routing key: " + routingKey);
            }
        } catch (Exception e) {
            log.error("[Error] RabbitMQ Routing Key is unknown >>> {}, message: {}", routingKey, e.getMessage());
        }
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                key,
                queuePushMessage
        );
    }
}
