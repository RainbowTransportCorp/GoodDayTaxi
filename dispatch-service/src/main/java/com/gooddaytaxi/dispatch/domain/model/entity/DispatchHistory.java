package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_dispatch_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DispatchHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", nullable = false)
    private UUID historyId;

    @Column(name = "dispatch_id", nullable = false)
    private UUID dispatchId;

    // 어떤 종류의 이벤트인지
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private HistoryEventType eventType;

    // 이전 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private DispatchStatus fromStatus;

    // 바뀐 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private DispatchStatus toStatus;

    // 누가 바꿨는가?
    @Enumerated(EnumType.STRING)
    @Column(name = "changed_by", nullable = false)
    private ChangedBy changedBy; // DRIVER / PASSENGER / SYSTEM / ADMIN

    @Column(name = "reason")
    private String reason;

    public static DispatchHistory recordStatusChange(
            UUID dispatchId,
            HistoryEventType type,
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
