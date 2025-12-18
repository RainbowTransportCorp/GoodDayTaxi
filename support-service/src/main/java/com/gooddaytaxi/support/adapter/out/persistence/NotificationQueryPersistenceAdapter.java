package com.gooddaytaxi.support.adapter.out.persistence;

import com.gooddaytaxi.support.application.port.out.persistence.NotificationSpecifications;
import com.gooddaytaxi.support.application.port.out.persistence.NotificationQueryPersistencePort;
import com.gooddaytaxi.support.application.service.AdminNotificationFilter;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA 기반 알림 리포지토리 구현체
 */
@RequiredArgsConstructor
@Component
public class NotificationQueryPersistenceAdapter implements NotificationQueryPersistencePort {

    private final NotificationJpaRepository notificationJpaRepository;

    /**
    * 알림 조회
    */
    @Override
    public Notification findById(UUID id) {
        return notificationJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("[Not Found] ⚠️ Notification Entity is not Found"));
    }

    @Override
    public Notification findByNotificationOriginId(UUID notificationOriginId) {
        return notificationJpaRepository.findByNotificationOriginId(notificationOriginId).orElseThrow(() -> new EntityNotFoundException("[Not Found] ⚠️ Notification Entity is not Found"));
    }

    @Override
    public List<Notification> findAllByNotifierId(UUID notifierId) {
        return notificationJpaRepository.findAllByNotifierId(notifierId);
    }

    @Override
    public List<Notification> findAllByNotifierIdIn(List<UUID> notifierIds) {
        return notificationJpaRepository.findAllByNotifierIdIn(notifierIds);
    }

    @Override
    public List<Notification> findAllByNotificationType(NotificationType notificationType) {
        return notificationJpaRepository.findAllByNotificationType(notificationType);
    }

    @Override
    public List<Notification> findByNotifiedAtBetween(LocalDateTime start, LocalDateTime end) {
        return notificationJpaRepository.findByNotifiedAtBetween(start, end);
    }

    @Override
    public List<Notification> findByNotifiedAtAfter(LocalDateTime start) {
        return notificationJpaRepository.findByNotifiedAtAfter(start);
    }

    @Override
    public List<Notification> findByNotifiedAtBefore(LocalDateTime end) {
        return notificationJpaRepository.findByNotifiedAtAfter(end);
    }

    @Override
    public List<Notification> findByIsReadFalseAndNotifiedAtAfter(LocalDateTime start) {
        return notificationJpaRepository.findByIsReadFalseAndNotifiedAtAfter(start);
    }

    @Override
    public List<Notification> findAll() {
        return notificationJpaRepository.findAll();
    }

    @Override
    public Page<Notification> search(AdminNotificationFilter filter, Pageable pageable) {
        return notificationJpaRepository.findAll(NotificationSpecifications.applyFilter(filter), pageable);
    }
}