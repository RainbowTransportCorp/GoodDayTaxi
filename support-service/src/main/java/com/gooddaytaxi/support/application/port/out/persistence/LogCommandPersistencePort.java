package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.domain.log.model.Log;
import com.gooddaytaxi.support.domain.notification.model.Notification;

public interface LogCommandPersistencePort {
    Log save(Log log);
}