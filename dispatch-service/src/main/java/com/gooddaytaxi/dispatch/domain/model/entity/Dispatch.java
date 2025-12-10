package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.application.exception.CannotAssignDriverException;
import com.gooddaytaxi.dispatch.domain.exception.DispatchAlreadyAcceptedException;
import com.gooddaytaxi.dispatch.domain.exception.InvalidDispatchStateException;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_dispatches")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dispatch_id", nullable = false)
    private UUID dispatchId;

    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    @Column(name = "destination_address", nullable = false, length = 255)
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispatch_status", nullable = false)
    private DispatchStatus dispatchStatus;

    @Column(name = "request_created_at", nullable = false)
    private LocalDateTime requestCreatedAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "timeout_at")
    private LocalDateTime timeoutAt;

    // ======== 정적 팩토리 메서드로 생성 ========
    public static Dispatch create(
            UUID passengerId,
            String pickupAddress,
            String destinationAddress
    ) {
        Dispatch d = new Dispatch();
        d.passengerId = passengerId;
        d.pickupAddress = pickupAddress;
        d.destinationAddress = destinationAddress;
        d.dispatchStatus = DispatchStatus.REQUESTED;
        d.requestCreatedAt = LocalDateTime.now();
        return d;
    }

    // ======== 상태 전이 ========

    public void cancel() {
        if (!isCancelableStatus()) {
            throw new InvalidDispatchStateException();
        }
        this.dispatchStatus = DispatchStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void accept() {
        if (this.dispatchStatus != DispatchStatus.ASSIGNING) {
            throw new DispatchAlreadyAcceptedException();
        }

        this.dispatchStatus = DispatchStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void startAssigning() {
        if (this.dispatchStatus != DispatchStatus.REQUESTED) {
            throw new InvalidDispatchStateException();
        }
        this.dispatchStatus = DispatchStatus.ASSIGNING;
    }

    public void assignedTo(UUID driverId) {
        if (this.dispatchStatus != DispatchStatus.ASSIGNING) {
            throw new CannotAssignDriverException();
        }
        this.dispatchStatus = DispatchStatus.ASSIGNED;
        this.driverId = driverId;
        this.assignedAt = LocalDateTime.now();
    }

    public void timeout() {
        if (!this.dispatchStatus.isWaiting()) return;

        this.dispatchStatus = DispatchStatus.TIMEOUT;
        this.timeoutAt = LocalDateTime.now();
    }

    private boolean isCancelableStatus() {
        return this.dispatchStatus == DispatchStatus.REQUESTED
                || this.dispatchStatus == DispatchStatus.ASSIGNING
                || this.dispatchStatus == DispatchStatus.ASSIGNED;
    }
}
