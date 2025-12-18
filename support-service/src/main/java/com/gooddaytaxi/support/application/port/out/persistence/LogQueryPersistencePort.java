package com.gooddaytaxi.support.application.port.out.persistence;

import com.gooddaytaxi.support.application.service.AdminLogFilter;
import com.gooddaytaxi.support.application.service.AdminNotificationFilter;
import com.gooddaytaxi.support.domain.log.model.Log;
import com.gooddaytaxi.support.domain.notification.model.Notification;
import com.gooddaytaxi.support.domain.notification.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LogQueryPersistencePort {
    Page<Log> search(AdminLogFilter filter, Pageable pageable);
}
