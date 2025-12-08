package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.commend.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.commend.DispatchRejectCommand;
import com.gooddaytaxi.dispatch.application.port.out.commend.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.result.*;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DriverDispatchService {

    private final DispatchCommandPort dispatchCommandPort;

    private final DispatchQueryPort dispatchQueryPort;


    /**
     * 배차 대기 (ASSIGNING) 상태 콜 조회 (기사)
     * @param userID
     * @return
     */
    @Transactional(readOnly = true)
    public List<DispatchPendingListResult> getDriverPendingDispatch(UUID userID) {

        log.info("[Driver Pending Dispatch] 조회 시작 - driverId={}", userID);

        List<Dispatch> dispatches = dispatchQueryPort.findByStatus(DispatchStatus.ASSIGNING);

        log.info("[Driver Pending Dispatch] 조회 완료 - driverId={}, count={}",
                userID, dispatches.size());

        dispatches.stream()
                .limit(5)
                .forEach(d -> log.debug("[PendingDispatch] id={}", d.getDispatchId()));

        return dispatches.stream()
                .map(dispatch -> DispatchPendingListResult.builder()
                        .dispatchId(dispatch.getDispatchId())
                        .pickupAddress(dispatch.getPickupAddress())
                        .destinationAddress(dispatch.getDestinationAddress())
                        .dispatchStatus(dispatch.getDispatchStatus())
                        .requestCreatedAt(dispatch.getRequestCreatedAt())
                        .build()
                )
                .toList();
    }


    /**
     * 콜 수락
     * @param command
     * @return
     */
    public DispatchAcceptResult accept(DispatchAcceptCommand command) {

        //권한 검증 로직 추가

        log.info("[Accept] 콜 수락 요청 수신 - driverId={}, dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());
        log.debug("[Accept] 조회된 Dispatch 엔티티 - id={}, status={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        // 상태 전이 (도메인 규칙 내부에서 검증)
        dispatch.accept();
        log.info("[Accept] Dispatch 상태 전이 완료 - id={}, newStatus={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        dispatchCommandPort.save(dispatch);
        log.info("[Accept] Dispatch 저장 완료 - id={}", dispatch.getDispatchId());

        LocalDateTime acceptedAt = LocalDateTime.now();

        DispatchAcceptResult result = DispatchAcceptResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(DispatchStatus.ACCEPTED)
                .acceptedAt(acceptedAt)
                .build();

        log.info("[Accept] 콜 수락 처리 완료 - dispatchId={}, acceptedAt={}",
                result.getDispatchId(), result.getAcceptedAt());

        return result;
    }

    /**
     * 콜 거절
     * @param command
     * @return
     */
    public DispatchRejectResult reject(DispatchRejectCommand command) {

        log.info("[Reject] 콜 거절 요청 수신 - driverId={}, dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = dispatchQueryPort.findById(command.getDispatchId());
        log.debug("[Reject] 조회된 Dispatch 엔티티 - id={}, status={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        // 상태 전이
        dispatch.cancel();
        log.info("[Reject] Dispatch 상태 전이 완료 - id={}, newStatus={}",
                dispatch.getDispatchId(), dispatch.getDispatchStatus());

        dispatchCommandPort.save(dispatch);
        log.info("[Reject] Dispatch 저장 완료 - id={}", dispatch.getDispatchId());

        LocalDateTime rejectedAt = LocalDateTime.now();

        DispatchRejectResult result = DispatchRejectResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(DispatchStatus.CANCELLED)
                .rejectedAt(rejectedAt)
                .build();

        log.info("[Reject] 콜 거절 처리 완료 - dispatchId={}, rejectedAt={}",
                result.getDispatchId(), result.getRejectedAt());

        return result;
    }
}
