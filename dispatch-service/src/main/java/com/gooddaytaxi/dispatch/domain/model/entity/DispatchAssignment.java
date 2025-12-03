package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.model.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_dispatch_assignments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DispatchAssignment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;   // ← camelCase로 변경

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

    /**
     * 기사 수락
     */
    public void accept() {
        this.assignmentStatus = AssignmentStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * 기사 거절
     */
    public void reject() {
        this.assignmentStatus = AssignmentStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * 기사 응답 없음 → 타임아웃
     */
    public void timeout() {
        this.assignmentStatus = AssignmentStatus.TIMEOUT;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * 처음 생성될 때(배차 시도) 초기값 설정 편의 메서드
     */
    public static DispatchAssignment create(UUID dispatchId, UUID candidateDriverId) {
        return DispatchAssignment.builder()
                .dispatchId(dispatchId)
                .candidateDriverId(candidateDriverId)
                .assignmentStatus(AssignmentStatus.SENT)
                .attemptedAt(LocalDateTime.now())
                .build();
    }
}

