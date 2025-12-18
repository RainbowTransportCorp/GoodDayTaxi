package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.input.log.ErrorDetectedCommand;
import com.gooddaytaxi.support.application.port.in.monitoring.NotifyErrorDetectUsecase;
import com.gooddaytaxi.support.application.port.out.internal.AccountDomainCommunicationPort;
import com.gooddaytaxi.support.application.port.out.messaging.NotificationPushMessagingPort;
import com.gooddaytaxi.support.application.port.out.dto.QueuePushMessage;
import com.gooddaytaxi.support.application.port.out.persistence.LogCommandPersistencePort;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationCommandPersistencePort;
import com.gooddaytaxi.support.domain.log.model.Log;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Log 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LogService implements NotifyErrorDetectUsecase {
    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final LogCommandPersistencePort logCommandPersistencePort;
    private final AccountDomainCommunicationPort accountDomainCommunicationPort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;

    /**
     * 수신자에게 탐지된 에러 정보 알림 서비스
     */
    @Transactional
    @Override
    public void execute(ErrorDetectedCommand command) {
        // Notification 생성 및 저장
        Notification notification = command.toEntity(NotificationType.ERROR_DETECTED);
        notification.assignIds(command.getDispatchId(), command.getTripId(), command.getPaymentId(), command.getDriverId(), command.getPassengerId());

        Notification savedNoti = notificationCommandPersistencePort.save(notification);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: notificationOriginId={}, notifierId={}, logType={}", savedNoti.getNotificationOriginId(), savedNoti.getNotifierId(), savedNoti.getNotificationType());

        // Log 생성 및 저장
        Log logging = command.toLogEntity(notification.getId());
        Log savedLog = logCommandPersistencePort.save(logging);

        // 수신자: [ MASTER_ADMIN 관리자들 ]
        List<UUID> receivers = accountDomainCommunicationPort.getMasterAdminUuids();

        // 알림 메시지 구성
        String messageTitle = "‼️서비스에 오류가 발생했습니다";
        Metadata metadata = command.getMetadata();
        String bySource;
        if (command.getSourceNotificationType() == null) {
            bySource = "아래와 같은 문제가 발생하였습니다";
        } else {
            bySource = "⚠️ " + command.getSourceNotificationType() + " 시점에 아래와 같은 문제가 발생하였습니다";
        }
        String messageBody = """
                [ %s 발생 ]
                ( %s )
                %s
                - 오류 메시지: %s
                - 관련 알림 ID: %s
                """.formatted(
                savedLog.getLogType(),
                command.getMetadata().occurredAt(),
                bySource,
                command.getMessage(),
                savedLog.getNotificationId()
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
        log.info("\uD83D\uDCE2 [Log] Created! notificationId={}, logType={}, message={}", notification.getId(), savedLog.getLogType(), savedLog.getLogMessage());

    }
}
