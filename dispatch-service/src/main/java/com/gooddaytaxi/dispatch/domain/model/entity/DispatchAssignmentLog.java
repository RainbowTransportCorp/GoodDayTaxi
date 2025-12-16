package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.exception.assignment.AssignmentAlreadyAcceptedException;
import com.gooddaytaxi.dispatch.domain.exception.assignment.AssignmentAlreadyRejectedException;
import com.gooddaytaxi.dispatch.domain.exception.assignment.AssignmentInvalidStateException;
import com.gooddaytaxi.dispatch.domain.model.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    /**
     * 재배차 attempt 번호
     * 같은 dispatch 의 n번째 배차 시도인지 구분
     * 재배차 때 기존 기사 제외 필터링 가능
     * 재시도 로그 분석 가능
     */
    @Column(name = "attempt_no", nullable = false)
    private int attemptNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false)
    private AssignmentStatus assignmentStatus;

    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;


    // ==== 생성 정팩메 (attemptNo 추가 버전) ====
    public static DispatchAssignmentLog create(UUID dispatchId, UUID driverId, int attemptNo) {
        DispatchAssignmentLog log = new DispatchAssignmentLog();
        log.dispatchId = dispatchId;
        log.candidateDriverId = driverId;
        log.assignmentStatus = AssignmentStatus.SENT;
        log.attemptedAt = LocalDateTime.now();
        log.attemptNo = attemptNo;
        return log;
    }

    // ==== 상태 전이 ====

    public void accept() {
        if (!assignmentStatus.isSent()) {
            throw new AssignmentInvalidStateException(
                    assignmentStatus,
                    "ACCEPTED로 변경"
            );
        }
        if (assignmentStatus.isAccepted()) {
            throw new AssignmentAlreadyAcceptedException();
        }
        this.assignmentStatus = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }


    public void reject() {
        if (!assignmentStatus.isSent()) {
            throw new AssignmentInvalidStateException(
                    assignmentStatus,
                    "REJECTED로 변경"
            );
        }
        if (assignmentStatus.isRejected()) {
            throw new AssignmentAlreadyRejectedException();
        }
        this.assignmentStatus = AssignmentStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }


    public void timeout() {
        if (!assignmentStatus.isSent()) {
            throw new AssignmentInvalidStateException(
                    assignmentStatus,
                    "TIMEOUT으로 변경"
            );
        }
        this.assignmentStatus = AssignmentStatus.TIMEOUT;
        this.respondedAt = LocalDateTime.now();
    }

}
