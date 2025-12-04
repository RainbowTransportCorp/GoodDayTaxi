package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.CreateCallCommand;
import com.gooddaytaxi.support.application.dto.DispatchAcceptCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.AcceptDispatchUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.RequestCallUsecase;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationCommandPersistencePort;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Dispatch 알림 서비스
 * Usecase 구현
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DispatchNotificationService implements RequestCallUsecase, AcceptDispatchUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationQueryPersistencePort notificationQueryPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;

    @Transactional
    @Override
    public void request(CreateCallCommand command) {

        // Notification 생성
        Notification noti = Notification.from(command, NotificationType.CALL_REQUEST);
        noti.assignIds(command.getDispatchId(), command.getDriverId(), command.getPassengerId(), null, null);
        notificationCommandPersistencePort.save(noti);

        // RabbitMQ로 driver, passenger에게 알림 Push
        List<UUID> receivers = new ArrayList<>();
        receivers.add(noti.getDriverId());
        receivers.add(noti.getPassengerId());
        notificationPushMessagingPort.send(receivers, "새로운 콜 요청이 도착했습니다!", noti.getMessage());

        // 로그
        log.info("\uD83D\uDCE2 [CALL-REQUEST] dispatchId={}, driverId={}, passengerId={} >>> {}",
                command.getDispatchId(),
                command.getDriverId(),
                command.getPassengerId(),
                command.getMessage());
    }

    @Transactional
    @Override
    public void handle(DispatchAcceptCommand command) {

    }
}
