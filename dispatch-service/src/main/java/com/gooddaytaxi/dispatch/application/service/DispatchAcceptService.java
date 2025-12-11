package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchAcceptedPayload;
import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchAcceptedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.TripCreateRequestCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptCommand;
import com.gooddaytaxi.dispatch.application.usecase.accept.DispatchAcceptResult;
import com.gooddaytaxi.dispatch.domain.exception.DispatchAlreadyAssignedByOthersException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import com.gooddaytaxi.dispatch.domain.service.DispatchDomainService;
import com.gooddaytaxi.dispatch.infrastructure.redis.DispatchLockManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DispatchAcceptService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final DispatchAssignmentLogService assignmentLogService;
    private final DispatchHistoryService historyService;

    private final DispatchAcceptedCommandPort acceptedEventPort;
    private final TripCreateRequestCommandPort tripEventPort;

    private final DispatchLockManager lockManager;
    private final DispatchDomainService domainService;

    private static final Logger log = LoggerFactory.getLogger(DispatchAcceptService.class);

    public DispatchAcceptResult accept(DispatchAcceptCommand command) throws InterruptedException {

        log.info("[Accept] 요청 수신 - driverId={} dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        String lockKey = command.getDispatchId().toString();
        if (!lockManager.tryLock(lockKey, command.getDriverId())) {
            log.warn("[Accept] 락 획득 실패 - 다른 기사가 먼저 처리");
            throw new DispatchAlreadyAssignedByOthersException();
        }

        try {
            Dispatch dispatch = queryPort.findById(command.getDispatchId());
            DispatchStatus before = dispatch.getDispatchStatus();

            DispatchAssignmentLog logEntry =
                    assignmentLogService.findLatest(command.getDispatchId(), command.getDriverId());

            domainService.processAccept(dispatch, logEntry, command.getDriverId());

            assignmentLogService.save(logEntry);
            commandPort.save(dispatch);

            historyService.saveStatusChange(
                    dispatch.getDispatchId(),
                    DispatchDomainEventType.ACCEPTED,
                    before,
                    dispatch.getDispatchStatus(),
                    ChangedBy.DRIVER
            );

            acceptedEventPort.publishAccepted(
                    DispatchAcceptedPayload.from(dispatch, command.getDriverId())
            );

            tripEventPort.publishTripCreateRequest(
                    TripCreateRequestPayload.from(dispatch)
            );

            log.info("[Accept] 완료 - dispatchId={}", dispatch.getDispatchId());
            return DispatchAcceptResult.builder()
                    .dispatchId(dispatch.getDispatchId())
                    .driverId(command.getDriverId())
                    .dispatchStatus(dispatch.getDispatchStatus())
                    .acceptedAt(dispatch.getAcceptedAt())
                    .build();

        } finally {
            lockManager.unlock(lockKey);
        }
    }
}
