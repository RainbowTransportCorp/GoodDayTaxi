package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationQueryPersistencePort {
    Notification findById(UUID id);
    Notification findByNotificationOriginId(UUID notificationOriginId);
    List<Notification> findAllByNotifierId(UUID notifierId);
    List<Notification> findAllByNotifierIdIn(List<UUID> notifierIds);
    List<Notification> findAllByNotificationType(NotificationType notificationType);
    List<Notification> findByNotifiedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Notification> findByNotifiedAtAfter(LocalDateTime start);
    List<Notification> findByNotifiedAtBefore(LocalDateTime end);
    List<Notification> findByIsReadFalseAndNotifiedAtAfter(LocalDateTime start);

    /*
	Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
    void updateActivities(Account account);
    */
}
