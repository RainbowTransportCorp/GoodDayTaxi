package com.gooddaytaxi.support.domain.notification.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * 알림 Entity - 시스템에서 발생하는 주요 이벤트 기반 알림
 */
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
    private NotificationType notificiationType;

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

    @Column(name = "message", nullable = false, length = 500)
    @Comment("알림 메시지")
    private String message;

    @Column(name = "is_read", nullable = false)
    @Comment("알림 읽음 여부")
    private boolean isRead;

    @Column(name = "notified_at")
    @Comment("알림 발생 시각")
    private LocalDateTime notifiedAt;


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
     * @param notifierId 알림 생성자
     * @param notificationOriginId 알림 생성 근원 도메인 ID
     * @param notificiationType 알림 타입
     * @param message 알림 메시지
     */
    @Builder
    public Notification(UUID notifierId, UUID notificationOriginId, NotificationType notificiationType, String message) {
        this.notifierId = notifierId;
        this.notificationOriginId = notificationOriginId;
        this.notificiationType = notificiationType;
        this.message = message;
        this.isRead = false;
        this.notifiedAt = LocalDateTime.now();
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
     *  - DISPATCH_XXX : dispatchId, passengerId, driverId 필요
     *  - TRIP_XXX : dispatchId, passengerId, driverId, tripId 필요
     *  - PAYMENT_XXX : dispatchId, passengerId, driverId, tripId, paymentId 필요
     *  - ERROR_DETECTED : 상황에 따라 필요
     *
     * @param dispatchId
     * @param passengerId
     * @param driverId
     * @param tripId
     * @param paymentId
     */
    public void setIds (UUID dispatchId, UUID passengerId, UUID driverId,
                        UUID tripId,
                        UUID paymentId) {
        this.dispatchId = dispatchId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.tripId = tripId;
        this.paymentId = paymentId;
    }




}


