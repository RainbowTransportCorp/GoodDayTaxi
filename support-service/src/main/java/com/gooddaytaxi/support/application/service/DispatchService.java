package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.DriverProfile;
import com.gooddaytaxi.support.adapter.out.internal.account.dto.VehicleInfo;
import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.dispatch.*;
import com.gooddaytaxi.support.application.port.in.dispatch.*;
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
 * Dispatch 알림 서비스
 * Usecase 구현
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DispatchService implements NotifyDispatchUsecase, NotifyCallAcceptUsecase, NotifyDispatchTimeoutUsecase, NotifyDispatchCancelUsecase, NotifyDispatchRejectUsecase, NotifyDispatchForceTimeoutUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;
    private final AccountDomainCommunicationPort accountDomainCommunicationPort;
//    private final NotificationAlertExternalPort notificationAlertExternalPort; (RabbitListener로 사용 시, 주석처리)

    /**
     * 수신자에게 배차 정보 알림 서비스
     */
    @Transactional
    @Override
    public void execute(NotifyDispatchInformationCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.DISPATCH_REQUESTED);
        notification.assignIds(command.getDispatchId(), null, null, command.getDriverId(), command.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, driverId={}, passengeId={}, message={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 콜 요청을 수락하시겠습니까?";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                %s
                승객 ID %s 님이 %s(으)로 향하는 운행을 요청하였습니다
                현재 승객의 위치는 %s 입니다
                """.formatted(
                savedNoti.getMessage(),
                savedNoti.getPassengerId(),
                command.getDestinationAddress(),
                command.getPickupAddress()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.debug("[Push] RabbitMQ 메시지: {}", messageTitle);

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [CALL] Requested! driverId={}, passengerId={}: {} >>> {}",command.getDriverId(), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());

    }

    /**
    * 수신자에게 수락된 콜 알림 서비스
    * */
    @Transactional
    @Override
    public void execute(NotifyDispatchAcceptedCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.DISPATCH_ACCEPTED);
        notification.assignIds(command.getDispatchId(), null, null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: dispatchId={}, driverId={}, passengerId={}, message={}", notification.getNotificationOriginId(), notification.getDriverId(), notification.getPassengerId(), notification.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, driverId={}, passengeId={}, message={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(null);
        receivers.add(savedNoti.getPassengerId());

        // Account Feign Client: 기사 정보 조회
        DriverProfile driverProfile = null;
        try {
            log.debug("[Connect] Support Service >>> Account Feign Starting . . . ");
            driverProfile = accountDomainCommunicationPort.getDriverInfo(savedNoti.getDriverId());
            log.debug("[Connect] DriverProfile from Account Feign: driverName={}, vehicleType={}, vehicleNumber={}", driverProfile.name(), driverProfile.vehicleInfo().vehicleType(), driverProfile.vehicleInfo().vehicleNumber());
        } catch (Exception e) {
            log.error("❌ [Error] Account API Feign Client Error: message={}, error={}", "Driver 조회 실패", e.getMessage());
        }

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 기사님이 콜을 수락했습니다";
        Metadata metadata = command.getMetadata();
        String messageBody;

        if (driverProfile != null) {
            String driverName = driverProfile.name();
            String phoneNumber = driverProfile.phoneNumber();
            VehicleInfo vehicle = driverProfile.vehicleInfo();
            String vehicleType = vehicle.vehicleType();
            String vehicleNumber = vehicle.vehicleNumber();
            String vehicleColor = vehicle.vehicleColor();

            messageBody = """
                [ %s ]
                %s 기사님이 %s → %s로
                안전하게 운행해주실 예정이오니, 차량 정보를 참고하여 대기하여 주십시오
                \uD83D\uDE95 탑승 차량:  %s의 %s(%s)
                Call: %s
                """.formatted(
                        savedNoti.getMessage(),
                        driverName,
                        command.getPickupAddress(),
                        command.getDestinationAddress(),
                        vehicleColor,
                        vehicleType,
                        vehicleNumber,
                        phoneNumber
                  );
        } else {
            messageBody = "택시 차량 정보를 가져오지 못했습니다. 다시 한 번 새로고침 해주세요";
        }

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.debug("[Push] RabbitMQ 메시지: {}", messageTitle);

         // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [CALL] Accepted! passengerId={}: {} >>> {}", queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }

    /**
     * 수신자에게 배차 시도 시간 초과 알림 서비스
     * */
    @Transactional
    @Override
    public void execute(NotifyDipsatchTimeoutCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.DISPATCH_TIMEOUT);
        notification.assignIds(command.getDispatchId(), null, null, null, command.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, passengeId={}, timeoutAt={}", savedNoti.getDispatchId(), savedNoti.getPassengerId(), command.getTimeoutAt());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(null);
        receivers.add(savedNoti.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 기사 매칭 시간이 초과되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                [ %s ]
                운행 중인 기사님께 배차를 시도하였지만
                시간(30s)이 초과되었습니다
                """.formatted(
                savedNoti.getMessage()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.debug("[Push] RabbitMQ 메시지: {}", messageTitle);

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [DISPATCH] Timeout! dispatchId={}, passengerId={}, timeoutAt={}", command.getDispatchId(), command.getPassengerId(), command.getTimeoutAt());

    }

    /**
     * 배차 취소 알림 서비스
     * */
    @Transactional
    @Override
    public void execute(NotifyDispatchCanceledCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.DISPATCH_CANCELED);
        notification.assignIds(command.getDispatchId(), null, null, command.getDriverId(), command.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, driverId={}, passengeId={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), savedNoti.getPassengerId());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 승객의 콜이 취소되었습니다";
        Metadata metadata = command.getMetadata();
        String canceledBy;

        switch(command.getCanceledBy()) {
            case "PASSENGER" -> canceledBy = "승객의 취소";
            case "SYSTEM" -> canceledBy = "시스템 상";
            default -> canceledBy = "기타 사유";

        }
        String messageBody = """
                %s %s
                %s(으)로 배차가 취소되었습니다
                """.formatted(
                command.getCanceledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                savedNoti.getMessage(),
                canceledBy
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.debug("[Push] RabbitMQ 메시지: {}", messageTitle);

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [DISPATCH] Canceled! dispatchId={}, driverId={}, passengerId={}, cancelledAt={}", command.getDispatchId(), command.getDriverId(), command.getPassengerId(), command.getCanceledAt());

    }

    /**
     * 배차 거절 알림 서비스
     * */
    @Transactional
    @Override
    public void execute(NotifyDispatchRejectedCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.DISPATCH_REJECTED);
        notification.assignIds(command.getDispatchId(), null, null, command.getDriverId(), command.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, driverId={}, passengeId={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), savedNoti.getPassengerId());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 콜이 거절되었습니다";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                %s에 %s
                다시 배차를 시도합니다
                """.formatted(
                command.getRejectedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                savedNoti.getMessage()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.debug("[Push] RabbitMQ 메시지: {}", messageTitle);

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [DISPATCH] Rejected! dispatchId={}, driverId={}, rejectedAt={}", command.getDispatchId(), command.getDriverId(), command.getRejectedAt());

    }

    /**
     * 강제 운행 종료 알림 서비스
     * */
    @Transactional
    @Override
    public void execute(NotifyDipsatchForceTimeoutCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.DISPATCH_FORCE_TIMEOUT);
        notification.assignIds(command.getDispatchId(), null, null, command.getDriverId(), null);

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, driverId={}, force={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), command.getForcedByRole());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(savedNoti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 운행을 시작할 수 없습니다.";
        Metadata metadata = command.getMetadata();
        String messageBody = """
                [ %s ]
                배차 %s 상태에서 %s
                (%s)
                """.formatted(
                command.getReason(),
                command.getPreviousStatus(),
                command.getMessage(),
                command.getForceTimeoutAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.debug("[Push] RabbitMQ 메시지: {}", messageTitle);

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 알림 전송 시각 할당
        savedNoti.assignMessageSendingTime(LocalDateTime.now());

        // 로그
        log.info("\uD83D\uDCE2 [DISPATCH] Force Timeout! dispatchId={}, driverId={}, forceTimeout={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), command.getForceTimeoutAt());
    }
}