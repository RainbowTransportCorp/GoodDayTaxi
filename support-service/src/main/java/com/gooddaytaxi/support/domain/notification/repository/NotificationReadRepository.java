//package com.gooddaytaxi.support.domain.notification.repository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import com.gooddaytaxi.support.domain.notification.model.Notification;
//
//
///**
// * 알림 조회 전용 리포지토리
// */
//public interface NotificationReadRepository {
//
//    /**
//     * 알림 ID로 알림 조회
//     *
//     * @param notificationId 조회할 알림 ID
//     * @return 알림 정보, 존재하지 않으면 empty Optional
//     */
//    Optional<Notification> findById(UUID notificationId);
//
//}