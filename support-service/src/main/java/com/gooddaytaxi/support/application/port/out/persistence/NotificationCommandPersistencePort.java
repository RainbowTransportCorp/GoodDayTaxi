package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.domain.notification.model.Notification;

public interface NotificationCommandPersistencePort {
    Notification save(Notification notification);
}
