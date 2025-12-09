package com.gooddaytaxi.support.adapter.out.persistence;

import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 알림 Spring Data JPA 리포지토리
 */
interface NotificationJpaRepository extends JpaRepository<Notification, UUID> {

    /** 알림 생성, 삭제
     *
     */
    Notification save(Notification notification);

    /** 알림 조회
     *
     */
    Optional<Notification> findById(UUID notificationId);
    Notification findByNotificationOriginId(UUID id);
    List<Notification> findAllByNotificationType(NotificationType type);
    List<Notification> findByNotifiedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByNotifiedAtAfter(LocalDateTime start);
    List<Notification> findByNotifiedAtBefore(LocalDateTime end);
    List<Notification> findByIsReadFalseAndNotifiedAtAfter(LocalDateTime start);


}
