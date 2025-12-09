package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.UserInfo;
import com.gooddaytaxi.support.application.dto.CreateDispatchInfoCommand;
import com.gooddaytaxi.support.application.dto.DispatchAcceptCommand;
import com.gooddaytaxi.support.application.port.out.internal.account.AccountDomainCommunicationPort;
import com.gooddaytaxi.support.application.port.in.dispatch.AcceptDispatchUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchUsecase;
import com.gooddaytaxi.support.application.port.out.external.NotificationAlertExternalPort;
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
public class DispatchNotificationService implements NotifyDispatchUsecase, AcceptDispatchUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationQueryPersistencePort notificationQueryPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;
    private final AccountDomainCommunicationPort accountDomainCommunicationPort;
    private final NotificationAlertExternalPort notificationAlertExternalPort;


    @Transactional
    @Override
    public void request(CreateDispatchInfoCommand command) {

        // Notification 생성
        Notification noti = Notification.from(command, NotificationType.DISPATCH_REQUESTED);
        noti.assignIds(null, command.getDriverId(), command.getPassengerId(), null, null);
        notificationCommandPersistencePort.save(noti);

        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());
        String messageTitle = "\uD83D\uDE95 새로운 콜 요청이 도착했습니다!";

        // RabbitMQ로 driver, passenger에게 알림 Push
        notificationPushMessagingPort.send(receivers, messageTitle, noti.getMessage());

        // Push 알림: Slack, FCM 등
//        PushMessage message = new PushMessage(receivers, messageTitle, noti.getMessage());
        UserInfo driver = accountDomainCommunicationPort.getUserInfo(receivers.get(0));
        List<String> slackReceivers = new ArrayList<>();
        slackReceivers.add(driver.slackUserId());
            // slack
        notificationAlertExternalPort.sendCallRequest(slackReceivers, messageTitle, noti.getMessage());

        // 로그
        log.info("\uD83D\uDCE2 [CALL-REQUEST] driverId={}, passengerId={} >>> {}",
//                command.getDispatchId(),
                command.getDriverId(),
                command.getPassengerId(),
                command.getMessage());
    }

    @Transactional
    @Override
    public void handle(DispatchAcceptCommand command) {

    }
}
