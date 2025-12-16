package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.application.dto.Metadata;
import com.gooddaytaxi.support.application.dto.*;
import com.gooddaytaxi.support.application.dto.payment.*;
import com.gooddaytaxi.support.application.port.in.payment.NotifyCompletedPaymentUsecase;
import com.gooddaytaxi.support.application.port.in.payment.NotifyRefundUsecase;
import com.gooddaytaxi.support.application.port.out.internal.account.AccountDomainCommunicationPort;
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
 * Payment 알림 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentService implements NotifyCompletedPaymentUsecase, NotifyRefundUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;
    private final AccountDomainCommunicationPort accountDomainCommunicationPort;

    /**
     * 수신자에게 결제 완료 알림 서비스
     */
    @Transactional
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
        receivers.add(noti.getDriverId());
        receivers.add(noti.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 [GoodDayTaxi] 결제가 정상적으로 처리되었습니다";
        Metadata metadata = command.getMetadata();

        String messageBody = """
                · 결제 금액: %,d원
                · 결제 수단: %s
                · 승인 일시: %s
                이용해 주셔서 감사합니다.
                """.formatted(
                    command.getAmount(),
                    command.getPaymentMethod(),
                    command.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
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


    /**
     * 수신자에게 환불 요청 알림 서비스
     */
    @Transactional
    @Override
    public void request(NotifyRefundRequestedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.REFUND_REQUEST_CREATED);
        noti.assignIds(null, command.getTripId(), command.getPaymentId(), command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: refundRequestId={}, paymentId={}, driverId={}, passengerId={}, message={}", noti.getNotificationOriginId(), noti.getPaymentId(), noti.getDriverId(), noti.getNotifierId(), noti.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: paymentId={}, driverId={}, passengerId={}, message={}", savedNoti.getPaymentId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ MASTER_ADMIN 관리자들 ]
        List<UUID> receivers = accountDomainCommunicationPort.getMasterAdminUuids();

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 고객님의 환불 요청이 접수되었습니다";
        Metadata metadata = command.getMetadata();

        String messageBody = """
                · 환불 요청 ID: %s
                · 결제 ID: %s
                · 요청 승객 ID: %s
                · 운행 기사 ID: %s
                · 환불 결제 수단:  %s
                · 환불 금액: %,d원
                · %s
                · '%s' (상)의 사유로 고객의 환불 요청이 접수되었습니다
                """.formatted(
                command.getRefundRequestId(),
                command.getPaymentId(),
                command.getNotifierId(), // == Passenger ID
                command.getDriverId(),
                command.getPaymentMethod(),
                command.getAmount(),
                command.getRequestedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                command.getReason()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "PAYMENT");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Refund] Requested! passengerId={}: {}(으)로 {}원 환불 요청", command.getNotifierId(), command.getPaymentMethod(), command.getAmount());
    }

    /**
     * 수신자에게 환불 요청 거절 알림 서비스
     */
    @Transactional
    @Override
    public void reject(NotifyRefundRejectedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.REFUND_REQUEST_REJECTED);
        noti.assignIds(null, command.getTripId(), command.getPaymentId(), null, command.getPassengerId());
        log.debug("[Check] Notification 생성: refundRequestId={}, paymentId={}, adminId={}, passengerId={}, rejectReason={}", noti.getNotificationOriginId(), noti.getPaymentId(), noti.getNotifierId(), noti.getPassengerId(), command.getRejectReason());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: paymentId={}, adminId={}, passengerId={}, rejectReason={}", savedNoti.getPaymentId(), savedNoti.getNotifierId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(null);
        receivers.add(noti.getPassengerId());

        // 알림 메시지 구성
        String messageTitle = "\uD83D\uDCE2 고객님의 환불 요청이 처리 기준에 따라 거절되었습니다";
        Metadata metadata = command.getMetadata();

        String messageBody = """
                고객님께서 환불 요청하신 지난 %s 운행 이력에 대해
                아래와 같은 사유로 검토 결과 승인되지 않았습니다. 죄송합니다.
                
                [ 환불 처리 불가 ]
                반려 시각: %s
                사유: %s
                
                서비스 이용에 불편을 드려 죄송합니다.
                궁금하신 사항은 고객센터로 문의하시면 안내해드리겠습니다.
                """.formatted(
                command.getTripId(),
                command.getRejectedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                command.getRejectReason()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "PAYMENT");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Refund] Rejected! passengerId={}: {} (상)의 사유로 환불 승인 거절", command.getNotifierId(), command.getRejectReason());
    }

    /**
     * 수신자에게 환불 완료 알림 서비스
     */
    @Transactional
    @Override
    public void complete(NotifyRefundCompletedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.REFUND_COMPLETED);
        noti.assignIds(null, command.getTripId(), command.getPaymentId(), command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: refundRequestId={}, paymentId={}, adminId={}, driverId={}, passengerId={}, refundReason={}", noti.getNotificationOriginId(), noti.getPaymentId(), noti.getNotifierId(), noti.getDriverId(), noti.getPassengerId(), command.getReason());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: paymentId={}, adminId={}, driverId={}, passengerId={}, message={}", savedNoti.getPaymentId(), savedNoti.getNotifierId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(noti.getDriverId());
        receivers.add(noti.getPassengerId());

        // 알림 메시지 구성
        RefundReason refundReason = RefundReason.of(command.getReason());
        String refundDetailReason = command.getMessage();

        String messageTitle = "\uD83D\uDCE2 환불 완료 - " + refundReason.getDescription();
        Metadata metadata = command.getMetadata();

        String messageContent;
        String refundTargetAmount;

        switch(refundReason) {
            case CUSTOMER_REQUEST -> {
                refundTargetAmount = "결제하신 금액";
                messageContent =
                        "이용 과정에서 불편을 느끼시게 된 점에 대해 다시 한 번 양해 부탁드리며,"
                        + "\n" +
                        "보다 만족스러운 서비스를 제공할 수 있도록 지속적으로 개선해 나가겠습니다.";
            }
            case COMPANY_FAULT_SYSTEM -> {
                refundTargetAmount = "결제하신 금액";
                messageContent =
                        "동일한 문제가 재발하지 않도록 시스템 점검 및 개선을 진행하고 있으며,"
                        + "\n" +
                        "이용에 불편을 드린 점 다시 한 번 깊이 양해 부탁드립니다.";
            }
            case DUPLICATE_PAYMENT -> {
                refundTargetAmount = "중복 결제된 금액";
                messageContent =
                        "향후 동일한 문제가 재발하지 않도록 결제 처리 로직을 점검·개선하고 있으며,"
                        + "\n" +
                        "이용에 불편을 드린 점 다시 한 번 깊이 양해 부탁드립니다.";
            }
            case PROMOTION_COMPENSATION -> {
                refundTargetAmount = "보상 대상 금액";
                messageContent =
                        "향후 동일한 문제가 재발하지 않도록 프로모션 적용 로직을 점검·개선하고 있으며,"
                        + "\n" +
                        "이용에 불편을 드린 점 다시 한 번 깊이 양해 부탁드립니다.";
            }
            case ADMIN_ADJUSTMENT -> {
                refundTargetAmount = "조정 대상 금액";
                messageContent =
                        "앞으로도 고객님의 이용 내역을 더욱 면밀히 확인하여"
                        + "\n" +
                        "보다 신뢰할 수 있는 서비스를 제공할 수 있도록 최선을 다하겠습니다.";
            }
            case ETC -> {
                refundTargetAmount = "환불 대상 금액";
                messageContent =
                        "이용에 불편을 드린 점 다시 한 번 깊이 양해 부탁드리며,"
                        + "\n" +
                        "보다 안정적인 서비스 제공을 위해 지속적으로 개선해 나가겠습니다.";

            }
            default -> {
                refundTargetAmount = "결제하신 금액";
                messageContent =
                        "서비스 이용에 진심으로 감사드리며,"
                        + "\n" +
                        "더 나은 경험을 제공할 수 있도록 최선을 다하겠습니다.";
            }
        }

        String messageBody = """
                고객님께 불편을 드려 진심으로 사과드립니다.
                
                %s
                
                %s에 환불 처리가 승인되어 현재 환불 처리 완료 상태이며,
                이에 따라 %s 금액은 결제하신 수단(%s)으로 환불될 예정입니다
                결제 수단에 따라 실제 환불 반영까지는 영업일 기준 일정 시간이 소요될 수 있습니다.
                
                %s
                """.formatted(
                refundDetailReason,
                command.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                refundTargetAmount,
                command.getMethod(),
                messageContent
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "PAYMENT");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Refund] Completed! refundRequestId={}: {} 처리 완료", command.getNotificationOriginId(), refundReason.getDescription());
    }

    /**
     * 수신자에게 환불 진행 요청 알림 서비스
     */
    @Transactional
    @Override
    public void createSettlement(NotifyRefundSettlementCreatedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.REFUND_SETTLEMENT_CREATED);
        noti.assignIds(null, command.getTripId(), command.getPaymentId(), command.getDriverId(), null);
        log.debug("[Check] Notification 생성: paymentId={}, adminId={}, driverId={}, reason={}", noti.getNotificationOriginId(), noti.getNotifierId(), noti.getDriverId(), command.getReason());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: paymentId={}, adminId={}, driverId={}, message={}", savedNoti.getPaymentId(), savedNoti.getNotifierId(), savedNoti.getDriverId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(noti.getDriverId());
        receivers.add(null);

        // 알림 메시지 구성

        String messageTitle = "\uD83D\uDCE2 승객의 환불 절차를 진행해주시기 바랍니다";
        Metadata metadata = command.getMetadata();

        String messageBody = """
                이전 운행 이력 중 ID %s인 운행에 대해 승객이 환불을 요청하였습니다
                'GoodDayTaxi'에서 %s에 아래 사유로 이를 승인하였기에
                직접 결제한 경우, 결제 이력(%s)에 대해 환불 절차를 진행해주시기 바랍니다
                
                [ 환불 승인 사유 ]
                %s
                
                [ 환불 금액 및 수단 ]
                · 환불 금액: %,d원
                · 환불 수단: %s
                """.formatted(
                command.getTripId(),
                command.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                command.getPaymentId(),
                command.getReason(),
                command.getAmount(),
                command.getMethod()
        );

        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, metadata, messageTitle, messageBody);
        notificationPushMessagingPort.push(queuePushMessage, "PAYMENT");
        log.debug("[Push] RabbitMQ 메시지: {}", queuePushMessage.title());

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [Refund] Settlement Created! paymentId={}: {}(으)로 {}원 환불 진행 요청", command.getNotificationOriginId(), command.getMethod(), command.getAmount());
    }
}
