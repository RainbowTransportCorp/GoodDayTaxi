package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchRejectedPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchRejectedCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectCommand;
import com.gooddaytaxi.dispatch.application.usecase.reject.DispatchRejectResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepository;
import com.gooddaytaxi.dispatch.domain.service.DispatchDomainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DispatchRejectService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final DispatchAssignmentLogService assignmentLogService;
    private final DispatchHistoryService historyService;

    private final DispatchRejectedCommandPort eventPort;
    private final DispatchDomainService domainService;

    private static final Logger log = LoggerFactory.getLogger(DispatchRejectService.class);

    public DispatchRejectResult reject(DispatchRejectCommand command) {

        log.info("[Reject] 요청 수신 - driverId={} dispatchId={}",
                command.getDriverId(), command.getDispatchId());

        Dispatch dispatch = queryPort.findById(command.getDispatchId());
        DispatchStatus before = dispatch.getDispatchStatus();

        DispatchAssignmentLog logEntry =
                assignmentLogService.findLatest(command.getDispatchId(), command.getDriverId());

        domainService.processReject(dispatch, logEntry, command.getDriverId());

        assignmentLogService.save(logEntry);
        commandPort.save(dispatch);

        historyService.saveStatusChange(
                dispatch.getDispatchId(),
                DispatchDomainEventType.REJECTED,
                before,
                dispatch.getDispatchStatus(),
                ChangedBy.DRIVER
        );

        eventPort.publishRejected(
                DispatchRejectedPayload.from(
                        dispatch.getDispatchId(),
                        command.getDriverId(),
                        LocalDateTime.now()
                )
        );

        log.info("[Reject] 완료 - dispatchId={}", dispatch.getDispatchId());

        return DispatchRejectResult.builder()
                .dispatchId(command.getDispatchId())
                .driverId(command.getDriverId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .rejectedAt(LocalDateTime.now())
                .build();
    }
}

