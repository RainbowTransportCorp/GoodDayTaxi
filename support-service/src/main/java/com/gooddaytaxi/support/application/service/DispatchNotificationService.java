package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.CreateDispatchInfoCommand;
import com.gooddaytaxi.support.application.dto.GetDispatchInfoCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.AcceptDispatchUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchUsecase;
import com.gooddaytaxi.support.application.port.out.external.NotificationAlertExternalPort;
import com.gooddaytaxi.support.application.port.out.internal.account.AccountDomainCommunicationPort;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import com.gooddaytaxi.support.application.port.out.messaging.QueuePushMessage;
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
//    private final NotificationAlertExternalPort notificationAlertExternalPort; (RabbitListener로 사용 시, 주석처리)


    @Transactional
    @Override
    public void request(CreateDispatchInfoCommand command) {
        log.info("‼️‼️‼️‼️Command 내용 확인title={}, body={}, driver={}, passenger={}",
                "새콜 요청", command.getMessage(), command.getDriverId(), command.getPassengerId());
        // Notification 생성
        Notification noti = Notification.from(command, NotificationType.DISPATCH_REQUESTED);
        noti.assignIds(command.getDispatchId(), command.getDriverId(), command.getPassengerId(), null, null);
        log.info("‼🤣🤣🤣🤣️ notification 객체={}", noti);
        notificationCommandPersistencePort.save(noti);
        log.info("‼🤣🤣🤣🤣️ notification 객체 in persistence={}", notificationQueryPersistencePort.findByNotificationOriginId(command.getDispatchId()));

        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());
        String messageTitle = "\uD83D\uDE95 새로운 콜 요청이 도착했습니다!";


        // RabbitMQ로 Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, messageTitle, noti.getMessage());
        notificationPushMessagingPort.send(queuePushMessage);
        log.info("‼️‼️‼️‼️QueuePush Message 내용 확인title={}, body={}, receivers={}",
                messageTitle, noti.getMessage(), receivers);


        // Push 알림: Slack, FCM 등
//        notificationAlertExternalPort.sendCallDirectRequest(queuePushMessage);// Slack 전송을 위한 RabbitMQ 직접 호출(비동기를 위해 직접 호출은 주석처리). RabbitListener가 알아서 호출

        // 로그
        log.info("\uD83D\uDCE2 [CALL-REQUEST] driverId={}, passengerId={} >>> {}",
//                command.getDispatchId(),
                command.getDriverId(),
                command.getPassengerId(),
                command.getMessage());
    }

    @Transactional
    @Override
    public void accept(GetDispatchInfoCommand command) {
        // Notification 생성
        Notification noti = Notification.from(command, NotificationType.DISPATCH_ACCEPTED);
        noti.assignIds(command.getDispatchId(), command.getDriverId(), command.getPassengerId(), null, null);
        notificationCommandPersistencePort.save(noti);

        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());
    }
}
