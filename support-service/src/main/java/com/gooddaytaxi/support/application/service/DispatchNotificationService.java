package com.gooddaytaxi.support.application.service;

import com.gooddaytaxi.support.adapter.out.internal.account.dto.DriverProfile;
import com.gooddaytaxi.support.adapter.out.internal.account.dto.VehicleInfo;
import com.gooddaytaxi.support.application.Metadata;
import com.gooddaytaxi.support.application.dto.NotifyDispatchAcceptedCommand;
import com.gooddaytaxi.support.application.dto.NotifyDispatchInformationCommand;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyAcceptedCallUsecase;
import com.gooddaytaxi.support.application.port.in.dispatch.NotifyDispatchUsecase;
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
public class DispatchNotificationService implements NotifyDispatchUsecase, NotifyAcceptedCallUsecase {

    private final NotificationCommandPersistencePort notificationCommandPersistencePort;
    private final NotificationQueryPersistencePort notificationQueryPersistencePort;
    private final NotificationPushMessagingPort notificationPushMessagingPort;
    private final AccountDomainCommunicationPort accountDomainCommunicationPort;
//    private final NotificationAlertExternalPort notificationAlertExternalPort; (RabbitListener로 사용 시, 주석처리)

    /**
     * 수신자에게 배차 정보 알림 서비스
     */
    @Transactional
    @Override
    public void execute(NotifyDispatchInformationCommand command) {
        log.info("‼️‼️‼️‼️Command 내용 확인title={}, body={}, driver={}, passenger={}",
                "새콜 요청", command.getMessage(), command.getDriverId(), command.getPassengerId());
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.DISPATCH_REQUESTED);
        noti.assignIds(command.getDispatchId(), null, null, command.getDriverId(), command.getPassengerId());
        log.info("‼🤣🤣🤣🤣️ notification 객체={}", noti);
        notificationCommandPersistencePort.save(noti);
        log.info("‼🤣🤣🤣🤣️ notification 객체 in persistence={}", notificationQueryPersistencePort.findByNotificationOriginId(command.getDispatchId()));

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(command.getDriverId());
        receivers.add(null);
        String messageTitle = "\uD83D\uDCE2 콜 요청을 수락하시겠습니까?";


        // RabbitMQ: Queue에 Push
        QueuePushMessage queuePushMessage = QueuePushMessage.create(receivers, command.getMetadata(), messageTitle, noti.getMessage());
        notificationPushMessagingPort.push(queuePushMessage, "DISPATCH");
        log.info("‼️‼️‼️‼️QueuePush Message 내용 확인title={}, body={}, receivers={}",
                messageTitle, noti.getMessage(), receivers);

        // Push 알림: Slack, FCM 등 - RabbitMQ Listener 없이 직접 호출 시 사용
//        notificationAlertExternalPort.sendDirectRequest(queuePushMessage);

        // 로그
        log.info("\uD83D\uDCE2 [CALL-REQUEST] driverId={}, passengerId={} >>> {}",
//                command.getDispatchId(),
                command.getDriverId(),
                command.getPassengerId(),
                command.getMessage());
    }

    /**
    * 수신자에게 수락된 콜 알림 서비스
    * */
    @Transactional
    @Override
    public void execute(NotifyDispatchAcceptedCommand command) {
        // Notification 생성 및 저장
        Notification noti = Notification.from(command, NotificationType.DISPATCH_ACCEPTED);
        noti.assignIds(command.getDispatchId(), null, null, command.getDriverId(), command.getPassengerId());
        log.debug("[Check] Notification 생성: dispatchId={}, driverId={}, passengerId={}, message={}", noti.getNotificationOriginId(), noti.getDriverId(), noti.getPassengerId(), noti.getMessage());

        Notification savedNoti = notificationCommandPersistencePort.save(noti);
//        Notification savedNoti = notificationQueryPersistencePort.findById(noti.getId());
        log.debug("[Check] Notification Persistence 조회: dispatchId={}, driverId={}, passengeId={}, message={}", savedNoti.getDispatchId(), savedNoti.getDriverId(), savedNoti.getPassengerId(), savedNoti.getMessage());

        // 수신자: [ 기사, 승객 ]
        List<UUID> receivers = new ArrayList<>();
        receivers.add(null);
        receivers.add(command.getPassengerId());

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
        String messageTitle = "\uD83D\uDCE2" + command.getMessage();
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
                %s 기사님이 콜을 수락했습니다
                %s >>> %s로
                안전하게 운행해주실 예정이오니, 차량 정보를 참고하여 대기하여 주십시오
                \uD83D\uDE95 탑승 차량:  %s의 %s(%s)
                Call: %s
                """.formatted(
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

        // 로그
        log.info("\uD83D\uDCE2 [CALL] driverId={}, passengerId={}: {} >>> {}",command.getDriverId(), queuePushMessage.receivers().get(1), command.getPickupAddress(), command.getDestinationAddress());
    }
}