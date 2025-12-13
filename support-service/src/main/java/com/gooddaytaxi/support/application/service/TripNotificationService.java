package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyTripStartedCommand;
import com.gooddaytaxi.support.application.port.in.trip.EndTripUsecase;
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
public class TripNotificationService implements NotifyStartedTripUsecase, EndTripUsecase {

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
        noti.assignIds(command.getDispatchId(), command.getDriverId(), command.getPassengerId(), command.getTripId(), null);
        log.debug("[Check] Notification 생성: tripId={}, driverId={}, message={}", noti.getNotificationOriginId(), noti.getNotifierId(), noti.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}, message={}", savedNoti.getTripId(), savedNoti.getDriverId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 운행이 시작되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                %s >>> %s로 출발합니다
                """.formatted(
                    command.getPickupAddress(),
                    command.getDestinationAddress()
                );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.send(queuePushMessage);
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendCallDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Started! driverId={}, passengerId={}: {} >>> {}",queuePushMessage.receivers().get(0), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }
}
