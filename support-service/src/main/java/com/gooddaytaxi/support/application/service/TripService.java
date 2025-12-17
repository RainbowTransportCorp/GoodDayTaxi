package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.trip.NotifyTripCanceledCommand;
import com.gooddaytaxi.support.application.dto.trip.NotifyTripEndedCommand;
import com.gooddaytaxi.support.application.dto.trip.NotifyTripStartedCommand;
import com.gooddaytaxi.support.application.port.in.trip.NotifyCanceledTripUsecase;
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

import java.time.format.DateTimeFormatter;
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
public class TripService implements NotifyStartedTripUsecase, NotifyEndedTripUsecase, NotifyCanceledTripUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;

    /**
     * 수신자에게 운행 시작 알림 서비스
     */
    @Transactional
    @Override
    public void execute(NotifyTripStartedCommand command) {
        // Notification 생성 및 저장
        Notification notification = Notification.from(command, NotificationType.TRIP_STARTED);
        notification.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: tripId={}, driverId={}, passengerId={}", notification.getNotificationOriginId(), notification.getDriverId(), notification.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}, passengerId={}", savedNoti.getTripId(), savedNoti.getDriverId(), savedNoti.getPassengerId());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(savedNoti.getPassengerId());

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
    @Transactional
    @Override
    public void execute(NotifyTripEndedCommand command) {
        // Notification 생성 및 저장
        Notification notification = Notification.from(command, NotificationType.TRIP_ENDED);
        notification.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: tripId={}, driverId={}, passengerId={}", notification.getNotificationOriginId(), notification.getDriverId(), notification.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}, passengerId={}", savedNoti.getTripId(), savedNoti.getDriverId(), savedNoti.getPassengerId());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(savedNoti.getPassengerId());

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

    /**
     * 수신자에게 운행 취소 알림 서비스
     */
    @Transactional
    @Override
    public void execute(NotifyTripCanceledCommand command) {
        // Notification 생성 및 저장
        Notification notification = Notification.from(command, NotificationType.TRIP_CANCELED);
        notification.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: tripId={}, driverId={}", notification.getNotificationOriginId(), notification.getDriverId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}", savedNoti.getTripId(), savedNoti.getDriverId());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 운행이 취소되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                [ %s ]의 사유로
                %s에 기사님의 운행이 취소되었습니다
                """.formatted(
                command.getCancelReason(),
                command.getCanceledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "TRIP");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Canceled! driverId={}, passengerId={}: {}",queuePushMessage.receivers().get(0), savedNoti.getPassengerId(), command.getCancelReason());
    }
}
