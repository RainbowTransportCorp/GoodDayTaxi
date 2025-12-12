package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_dispatch_assignment_logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DispatchAssignmentLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;

    @Column(name = "dispatch_id", nullable = false)
    private UUID dispatchId;

    @Column(name = "candidate_driver_id", nullable = false)
    private UUID candidateDriverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false)
    private AssignmentStatus assignmentStatus;

    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // ==== 생성 정팩메 ====
    public static DispatchAssignmentLog create(UUID dispatchId, UUID driverId) {
        DispatchAssignmentLog log = new DispatchAssignmentLog();
        log.dispatchId = dispatchId;
        log.candidateDriverId = driverId;
        log.assignmentStatus = AssignmentStatus.SENT;
        log.attemptedAt = LocalDateTime.now();
        return log;
    }

    // ==== 상태 전이 ====

    /** 기사 수락 */
    public void accept() {

        if (!isSent()) {
            throw new IllegalStateException(
                    "SENT 상태에서만 ACCEPTED로 상태 전이가 가능합니다. 현재 상태: " + this.assignmentStatus
            );
        }

        this.assignmentStatus = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    /** 기사 거절 */
    public void reject() {

        if (!isSent()) {
            throw new IllegalStateException(
                    "SENT 상태에서만 REJECTED로 상태 전이가 가능합니다. 현재 상태: " + this.assignmentStatus
            );
        }

        this.assignmentStatus = AssignmentStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    /** 응답 없음 → 타임아웃 */
    public void timeout() {

        if (!isSent()) {
            throw new IllegalStateException(
                    "SENT 상태에서만 TIMEOUT으로 상태 전이가 가능합니다. 현재 상태: " + this.assignmentStatus
            );
        }

        this.assignmentStatus = AssignmentStatus.TIMEOUT;
        this.respondedAt = LocalDateTime.now();
    }


    public boolean isSent() {
        return this.assignmentStatus == AssignmentStatus.SENT;
    }

    public boolean isAccepted() {
        return this.assignmentStatus == AssignmentStatus.ACCEPTED;
    }

    public boolean isRejected() {
        return this.assignmentStatus == AssignmentStatus.REJECTED;
    }

    public boolean isTimeout() {
        return this.assignmentStatus == AssignmentStatus.TIMEOUT;
    }

}
