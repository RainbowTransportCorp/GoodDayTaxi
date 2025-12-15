package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyTripEndedCommand;
import com.gooddaytaxi.support.application.dto.NotifyTripStartedCommand;
import com.gooddaytaxi.support.application.port.in.trip.NotifyEndedTripUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyStartedTripUsecase;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import com.gooddaytaxi.support.application.port.out.messaging.QueuePushMessage;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationCommandPersistencePort;
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
 * Trip 알림 서비스
 *  - Usecase 구현
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TripNotificationService implements NotifyStartedTripUsecase, NotifyEndedTripUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;

    /**
     * 수신자에게 운행 시작 알림 서비스
     */
    @Transactional
    @Override
    public void execute(NotifyTripStartedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.TRIP_STARTED);
        noti.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: tripId={}, driverId={}, passengerId={}, message={}", noti.getNotificationOriginId(), noti.getDriverId(), noti.getPassengerId(), noti.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}, passengerId={}, message={}", savedNoti.getTripId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 운행이 시작되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                %s → %s로 출발합니다
                """.formatted(
                    command.getPickupAddress(),
                    command.getDestinationAddress()
                );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "TRIP");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Started! driverId={}, passengerId={}: {} >>> {}",queuePushMessage.receivers().get(0), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }

    /**
     * 수신자에게 운행 종료 알림 서비스
     */
    @Override
    public void execute(NotifyTripEndedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.TRIP_ENDED);
        noti.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: tripId={}, driverId={}, passengerId={}, message={}", noti.getNotificationOriginId(), noti.getDriverId(), noti.getPassengerId(), noti.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}, passengerId={}, message={}", savedNoti.getTripId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 운행이 종료되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                %s → %s에 도착했습니다
                """.formatted(
                command.getPickupAddress(),
                command.getDestinationAddress()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "TRIP");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Ended! driverId={}, passengerId={}: {} >>> {}",queuePushMessage.receivers().get(0), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }
}
