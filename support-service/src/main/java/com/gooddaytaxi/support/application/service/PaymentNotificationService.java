package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyPaymentCompletedCommand;
import com.gooddaytaxi.support.application.port.in.payment.NotifyCompletedPaymentUsecase;
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
 * Payment 알림 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentNotificationService implements NotifyCompletedPaymentUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;

    /**
     * 수신자에게 결제 완료 알림 서비스
     */
    @Override
    public void execute(NotifyPaymentCompletedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.PAYMENT_COMPLETED);
        noti.assignIds(null, command.getTripId(), command.getPaymentId(), command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: paymentId={}, driverId={}, passengerId={}, message={}", noti.getNotificationOriginId(), noti.getDriverId(), noti.getPassengerId(), noti.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: paymentId={}, driverId={}, passengerId={}, message={}", savedNoti.getPaymentId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(command.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 [GoodDayTaxi] 결제가 정상적으로 처리되었습니다";
        Metadata metadata = command.getMetadata();

        String messageBody = """
                · 결제 금액: %,d원
                · 결제 수단: %s
                이용해 주셔서 감사합니다.
                """.formatted(
                    command.getAmount(),
                    command.getPaymentMethod()
                );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "PAYMENT");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Payment] Completed! driverId={}, passengerId={}: {}(으)로 결제 완료",queuePushMessage.receivers().get(0), queuePushMessage.receivers().get(1), command.getPaymentMethod());
    }
}
