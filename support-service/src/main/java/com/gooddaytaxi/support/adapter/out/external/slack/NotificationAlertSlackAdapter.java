package com.gooddaytaxi.support.adapter.out.external.slack;

import com.gooddaytaxi.support.adapter.out.messaging.config.RabbitMQConfig;
import com.gooddaytaxi.support.application.port.out.external.NotificationAlertExternalPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationAlertSlackAdapter implements NotificationAlertExternalPort {

    @RabbitListener(queues = RabbitMQConfig.DISPATCH_QUEUE)
    @Override
    public void sendCallRequest(String driverSlackId, List<UUID> receiverIds, String title, String body) {

    }

}
