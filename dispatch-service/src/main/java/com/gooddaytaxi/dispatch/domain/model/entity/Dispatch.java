package com.gooddaytaxi.dispatch.domain.model.entity;

import com.gooddaytaxi.common.jpa.model.BaseEntity;
import com.gooddaytaxi.dispatch.domain.exception.dispatch.DispatchAlreadyAssignedByOthersException;
import com.gooddaytaxi.dispatch.domain.exception.dispatch.DispatchCannotAssignException;
import com.gooddaytaxi.dispatch.domain.exception.dispatch.DispatchCannotCancelException;
import com.gooddaytaxi.dispatch.domain.exception.dispatch.DispatchInvalidStateException;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    //낙관적 락을 위한 버전필드
    @Version
    @Column(name = "version", nullable = false)
    private Long version;


    @Column(name = "passenger_id", nullable = false)
    private UUID passengerId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    @Column(name = "destination_address", nullable = false, length = 255)
    private String destinationAddress;

    // 재배차 정책을 위해 Dispatch 전체의 재배차 시도를 저장
    // 1) 동일 기사 반복 선택 방지 2) 재시도 횟수 제한 정책 가능 3) attemptNo 부여 기준
    @Column(name = "reassign_attempt_count", nullable = false)
    private int reassignAttemptCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "dispatch_status", nullable = false)
    private DispatchStatus dispatchStatus;

    @Column(name = "request_created_at", nullable = false)
    private LocalDateTime requestCreatedAt;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "timeout_at")
    private LocalDateTime timeoutAt;

    /**
     * 배차 생성을 위한 정적 팩토리 메서드
     *
     * @param passengerId        요청 승객 식별자
     * @param pickupAddress      요청하는 출발지 정보
     * @param destinationAddress 요청하는 도착지 정보
     * @return 생성된 배차 정보
     */
    public static Dispatch create(UUID passengerId, String pickupAddress, String destinationAddress) {
        Dispatch d = new Dispatch();
        d.passengerId = passengerId;
        d.pickupAddress = pickupAddress;
        d.destinationAddress = destinationAddress;
        d.dispatchStatus = DispatchStatus.REQUESTED;
        d.requestCreatedAt = LocalDateTime.now();
        return d;
    }


// ======== 상태 전이 ========

    public void startAssigning() {
        if (dispatchStatus != DispatchStatus.REQUESTED) {
            throw DispatchInvalidStateException.cannot(dispatchStatus, "배차 시작");
        }
        this.dispatchStatus = DispatchStatus.ASSIGNING;
    }


    public void assignedTo(UUID driverId) {
        if (dispatchStatus != DispatchStatus.ASSIGNING) {
            throw new DispatchCannotAssignException("배차 상태가 ASSIGNING일 때만 기사 배정이 가능합니다. 현재 상태=" + dispatchStatus);
        }

        this.dispatchStatus = DispatchStatus.ASSIGNED;
        this.driverId = driverId;
        this.assignedAt = LocalDateTime.now();
    }


    public void accept() {
        if (dispatchStatus != DispatchStatus.ASSIGNED) {
            throw new DispatchAlreadyAssignedByOthersException();
        }

        this.dispatchStatus = DispatchStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * 승객에 의한 취소로 상태 전이
     */
    public void cancelByPassenger() {
        if (dispatchStatus.isTerminal()) {
            throw new DispatchCannotCancelException();
        }

        this.dispatchStatus = DispatchStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    /**
     * 기사 1명이 배차 요청을 거절
     * - ASSIGNING 상태에서만 가능
     */
    public void rejectedByDriver() {
        if (dispatchStatus != DispatchStatus.ASSIGNING) {
            throw DispatchInvalidStateException.cannot(dispatchStatus, "기사 거절");
        }
    }

    public void increaseReassignAttempt() {
        this.reassignAttemptCount++;
    }

    public void timeout() {
        if (!dispatchStatus.canTimeout()) {

            throw DispatchInvalidStateException.cannot(
                    dispatchStatus,
                    "이미 종료되었거나 확정된 배차는 타임아웃 처리할 수 없습니다"
            );
        }

        this.dispatchStatus = DispatchStatus.TIMEOUT;
        this.timeoutAt = LocalDateTime.now();
    }

    public void forceTimeout() {
        if (!dispatchStatus.canForceTimeout()) {
            throw DispatchInvalidStateException.cannot(dispatchStatus, "강제 타임아웃");
        }

        this.dispatchStatus = DispatchStatus.TIMEOUT;
        this.timeoutAt = LocalDateTime.now();
    }

    public boolean isReassignTimeout(long elapsedSinceLastUpdateSeconds,
                                     long reassignTimeoutSeconds) {
        return dispatchStatus.isReassignable()
                && elapsedSinceLastUpdateSeconds >= reassignTimeoutSeconds;
    }

    // ======== Trip 연계 상태 전이 ========

    /**
     * Trip 서비스에 운행 생성을 요청한 상태로 전이
     * (Dispatch ACCEPTED → TRIP_REQUEST)
     */
    public void markTripRequest() {

        // 이미 Trip 요청을 보냈거나 운행 대기중이라면 요청 불가
        if (dispatchStatus.isWaitingTripReady() || dispatchStatus.isTripReady()) {
            throw DispatchInvalidStateException.cannot(
                dispatchStatus,
                "운행 요청"
            );
        }

        this.dispatchStatus = DispatchStatus.TRIP_REQUEST;
    }


    /**
     * TripReady 이벤트 수신 시 호출
     * (TRIP_REQUESTED → TRIP_READY)
     */
    public void markTripReady() {
        if (!dispatchStatus.isWaitingTripReady()) {
            throw DispatchInvalidStateException.cannot(
                dispatchStatus,
                "운행 대기"
            );
        }
        this.dispatchStatus = DispatchStatus.TRIP_READY;
    }

}
