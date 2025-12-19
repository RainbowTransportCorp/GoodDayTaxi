package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.trip.TripCanceledCommand;
import com.gooddaytaxi.support.application.dto.input.trip.TripEndedCommand;
import com.gooddaytaxi.support.application.dto.input.trip.TripLocationUpdatedCommand;
import com.gooddaytaxi.support.application.dto.input.trip.TripStartedCommand;
import com.gooddaytaxi.support.application.port.in.trip.NotifyTripCancelUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyTripEndUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyTripStartUsecase;
import com.gooddaytaxi.support.application.port.in.trip.NotifyTripLocationUpdateUsecase;
import com.gooddaytaxi.support.application.port.out.internal.AccountDomainCommunicationPort;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import com.gooddaytaxi.support.application.port.out.dto.QueuePushMessage;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationCommandPersistencePort;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
public class TripLocationUpdateEndStartCancelService implements NotifyTripStartUsecase, NotifyTripEndUsecase, NotifyTripCancelUsecase, NotifyTripLocationUpdateUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;
    private final AccountDomainCommunicationPort accountDomainCommunicationPort;

    /**
     * 수신자에게 운행 시작 알림 서비스
     */
    @Transactional
    @Override
    public void execute(TripStartedCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.TRIP_STARTED);
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

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Started! driverId={}, passengerId={}: {} >>> {}",queuePushMessage.receivers().get(0), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }

    /**
     * 수신자에게 운행 종료 알림 서비스
     */
    @Transactional
    @Override
    public void execute(TripEndedCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.TRIP_ENDED);
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

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Ended! driverId={}, passengerId={}: {} >>> {}",queuePushMessage.receivers().get(0), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }

    /**
     * 수신자에게 운행 취소 알림 서비스
     */
    @Transactional
    @Override
    public void execute(TripCanceledCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.TRIP_CANCELED);
        notification.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: tripId={}, driverId={}", notification.getNotificationOriginId(), notification.getDriverId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}", savedNoti.getTripId(), savedNoti.getDriverId());

        // 취소 사유로 수신자, 알림 메시지 구분
        Metadata metadata = command.getMetadata();
        String cancelReason = command.getCancelReason();
        List<UUID> receivers;
        String messageTitle;
        String messageBody;
        switch (cancelReason) {
            // 손님 요청으로 취소: 기사에게 송신
            case "PASSENGER_REQUEST" -> {
                // 수신자: [ 기사 ]
                receivers = new ArrayList<>();
                receivers.add(savedNoti.getDriverId());
                receivers.add(null);

                // 알림 메시지 구성
                messageTitle = "\uD83D\uDCE2 운행이 취소되었습니다.";
                messageBody = """
                %s
                승객의 요청으로 기사님의 운행이 취소되었습니다.
                """.formatted(
                        command.getCanceledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
            }
            // 기사 요청으로 취소: 손님에게 송신
            case "DRIVER_REQUEST" -> {
                // 수신자: [ 승객 ]
                receivers = new ArrayList<>();
                receivers.add(null);
                receivers.add(savedNoti.getPassengerId());

                // 알림 메시지 구성
                messageTitle = "\uD83D\uDCE2 운행이 취소되었습니다.";
                messageBody = """
                %s
                기사님의 요청으로 고객님의 콜이 취소되었습니다.
                - 사유: 차량 고장 및 정비 중 | 사고 발생 | 도로 상황 주의 등
                """.formatted(
                        command.getCanceledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
            }
            // 이외 사유로 취소: 기사, 손님, 관리자 모두에게 송신
            default -> {
                // 수신자: [ MASTER_ADMIN 관리자들 , 기사, 승객 ]
                receivers = accountDomainCommunicationPort.getMasterAdminUuids();
                receivers.add(savedNoti.getDriverId());
                receivers.add(savedNoti.getPassengerId());

                // 알림 메시지 구성
                messageTitle = "\uD83D\uDCE2 운행이 취소되었습니다.";
                messageBody = """
                [ %s ]의 사유로
                %s에 운행이 취소되었습니다.
                빠르게 복구될 수 있도록 조치하겠습니다. 죄송합니다.
                """.formatted(
                        command.getCancelReason(),
                        command.getCanceledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
            }
        }

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "TRIP");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Canceled! driverId={}, passengerId={}: {}",queuePushMessage.receivers().get(0), savedNoti.getPassengerId(), command.getCancelReason());
    }


    /**
     * 수신자에게 운행 목적지 변경 알림 서비스
     */
    @Transactional
    @Override
    public void execute(TripLocationUpdatedCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.TRIP_LOCATION_UPDATED);
        notification.assignIds(command.getDispatchId(), command.getTripId(), null, command.getDriverId(), null);
        log.debug("[Check] Notification 생성: tripId={}, driverId={}, currentAddress={}", notification.getNotificationOriginId(), notification.getDriverId(), command.getCurrentAddress());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: tripId={}, driverId={}, currentAddress={}", savedNoti.getTripId(), savedNoti.getDriverId(), command.getCurrentAddress());

        // 수신자: [ 기사 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 목적지가 변경되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                %s 일시에
                원래의 목적지 %s에서
                %s의 %s로 도착지가 변경되었습니다
                """.formatted(
                command.getLocationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                command.getPreviousRegion(),
                command.getRegion(),
                command.getCurrentAddress()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "TRIP");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [Trip] Location Updated! driverId={}, previousRegion: {} → currentAddress: {}",queuePushMessage.receivers().get(0), command.getPreviousRegion(), command.getCurrentAddress());
    }
}