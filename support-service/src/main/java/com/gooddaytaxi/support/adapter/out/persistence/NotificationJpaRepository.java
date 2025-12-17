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

    /**
     * 알림 생성, 삭제
     */
    Notification save(Notification notification);

    /**
     * 알림 조회
     */
    Optional<Notification> findById(UUID notificationId);
    Optional<Notification> findByNotificationOriginId(UUID id);
    List<Notification> findAllByNotifierId(UUID notifierId);
    /*
    * JPQL: findAllByNotifierIdIn
    @Query("""
        SELECT n
        FROM Notification n
        WHERE n.notifierId = :userId
           OR n.notifierId = :systemId
    """)
        List<Notification> findAllByNotifierIn(
                @Param("userId") UUID userId,
                @Param("systemId") UUID systemId
        );
    */
    List<Notification> findAllByNotifierIdIn(List<UUID> notifierIds);

    List<Notification> findAllByNotificationType(NotificationType type);
    List<Notification> findByNotifiedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByNotifiedAtAfter(LocalDateTime start);
    List<Notification> findByNotifiedAtBefore(LocalDateTime end);
    List<Notification> findByIsReadFalseAndNotifiedAtAfter(LocalDateTime start);
    List<Notification> findAll();
}
