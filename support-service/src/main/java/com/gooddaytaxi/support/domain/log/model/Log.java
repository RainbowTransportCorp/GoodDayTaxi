package com.gooddaytaxi.support.domain.log.model;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * 로그 Entity - 장애 의심 상태에 대한 기록
 */
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="p_support_system_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "log_id")
    private UUID id;

    @Column(name = "log_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LogType logType;

    @Column(name = "log_message", nullable = false, length = 500)
    private String logMessage;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "notification_id")
    private UUID notificationId;


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
     * 로그 Entity 생성
     *
     * @param logType 로그 타입
     * @param logMessage 로그 메시지
     */
    @Builder
    public Log(LogType logType, String logMessage) {
        this.logType = logType;
        this.logMessage = logMessage;
        this.occurredAt = LocalDateTime.now();
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
     * 알림 ID Setter - 알림 관련 장애인 경우 필요
     *
     * @param notificationId 알림 ID
     */
    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }
}
