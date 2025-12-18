package com.gooddaytaxi.support.domain.notification.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 알림 Entity - 시스템에서 발생하는 주요 이벤트 기반 알림
 */
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="p_support_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notificationId")
    private UUID id;

    @Column(name = "notifier_id", nullable = false)
    @Comment("알림 생성자 ID")
    private UUID notifierId;

    @Column(name = "notification_origin_id", nullable = false)
    @Comment("알림 생성 근원 도메인 ID")
    private UUID notificationOriginId;

    @Column(name = "notificiation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "dispatch_id")
    private UUID dispatchId;

    @Column(name = "passenger_id")
    private UUID passengerId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "trip_id")
    private UUID tripId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "message", length = 500)
    @Comment("알림 메시지")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Comment("알림 읽음 여부")
    private boolean isRead;

    @Column(name = "notified_at")
    @Comment("알림 발생 시각")
    private LocalDateTime notifiedAt;

//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(
//            name = "p_support_notification_receivers",
//            joinColumns = @JoinColumn(name = "notification_id")
//    )
//    @Column(name = "receiver_id")
//    @Comment("알림 수신자")
//    private List<UUID> receivers;

    /**
     * 논리적 삭제가 필요한 Domain의 Auditing 필드
     */
    @Column(name = "deleted_at")
    @Comment("삭제 시각")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    @Comment("삭제자 ID")
    private UUID deletedBy;


    /**
     * 알림 Entity 생성
     *
     * @param notificationOriginId 알림 생성 근원 도메인 ID
     * @param notifierId           알림 생성자
     * @param notificationType     알림 타입
     * @param message              알림 메시지
     */
    private Notification(UUID notificationOriginId, UUID notifierId, NotificationType notificationType, String message) {
        this.notificationOriginId = notificationOriginId;
        this.notifierId = notifierId;
        this.notificationType = notificationType;
        this.message = (message == null || message.isBlank()) ? null : message;
        this.isRead = false;
    }

    public static Notification create(UUID notificationOriginId, UUID notifierId, NotificationType notificationType, String message) {
        return new Notification(notificationOriginId, notifierId, notificationType, message);
    }


    /**
     * 알림 소프트 삭제
     *
     * @param deletedBy 삭제한 사용자 ID
     */
    public void softDelete(UUID deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }


    /**
     * 알림 타입에 따른 필요한 정보(ID) 설정
     * - DISPATCH_XXX : dispatchId, passengerId, driverId 필요
     * - TRIP_XXX : dispatchId, passengerId, driverId, tripId 필요
     * - PAYMENT_XXX : dispatchId, passengerId, driverId, tripId, paymentId 필요
     * - ERROR_DETECTED : 상황에 따라 필요
     *
     * @param dispatchId  배차 ID
     * @param passengerId 승객 ID
     * @param driverId    기사 ID
     * @param tripId      운행 ID
     * @param paymentId   결제 ID
     */
    public void assignIds(UUID dispatchId, UUID tripId, UUID paymentId, UUID driverId, UUID passengerId) {
        this.dispatchId = dispatchId;
        this.tripId = tripId;
        this.paymentId = paymentId;
        this.driverId = driverId;
        this.passengerId = passengerId;
    }

    /**
     * 알림 읽음 처리
     */
    public void updateIsRead() {
        this.isRead = true;
    }

    /**
     * 메시지 전송 시각 설정
     * @param sendingTime 메시지 전송 시각
     */
    public void assignMessageSendingTime(LocalDateTime sendingTime) {
        this.notifiedAt = sendingTime;
    }
}