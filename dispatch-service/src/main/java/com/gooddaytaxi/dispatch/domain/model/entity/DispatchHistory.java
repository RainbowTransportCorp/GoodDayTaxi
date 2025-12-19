package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_dispatch_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispatchHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", nullable = false)
    private UUID historyId;

    @Column(name = "dispatch_id", nullable = false)
    private UUID dispatchId;

    /**
     * 상태 변경의 원인이 되는 히스토리 이벤트 유형.
     *
     * 외부(DTO, 메시지) 및 DB 저장 시에는 String으로 관리하여
     * 확장과 변경에 유연하게 대응하고,
     * 코드 레벨에서는 Enum을 사용해 의미와 사용 범위를 제한한다.
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private DispatchStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private DispatchStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "changed_by", nullable = false)
    private ChangedBy changedBy; // DRIVER / PASSENGER / SYSTEM / ADMIN

    @Column(name = "reason")
    private String reason;

    /**
     * 배차 상태가 변경될 때
     * 히스토리 저장을 위한 정적 팩토리 메서드
     * @param dispatchId 변경된 특정 배차 식별자
     * @param type 변경된 이벤트의 유형
     * @param fromStatus 변경 전의 배차 상태
     * @param toStatus 변경 후의 배차 상태
     * @param changedBy 변경을 한 유저의 권한
     * @param reason 변경 사유
     * @return 변경되어 저장된 히스토리 정보
     */
    public static DispatchHistory recordStatusChange(
            UUID dispatchId,
            String type,
            DispatchStatus fromStatus,
            DispatchStatus toStatus,
            ChangedBy changedBy,
            String reason
    ) {
        DispatchHistory history = new DispatchHistory();
        history.dispatchId = dispatchId;
        history.eventType = type;
        history.fromStatus = fromStatus;
        history.toStatus = toStatus;
        history.changedBy = changedBy;
        history.reason = reason;
        return history;
    }

}
