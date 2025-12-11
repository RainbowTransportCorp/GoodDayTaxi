package com.gooddaytaxi.dispatch.application.service;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchCanceledPayload;
import com.gooddaytaxi.dispatch.application.exception.auth.DispatchPassengerPermissionValidator;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCanceledCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DispatchCancelService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final DispatchCanceledCommandPort eventPort;
    private final DispatchHistoryService historyService;

    private final DispatchPassengerPermissionValidator dispatchPassengerPermissionValidator;

    private static final Logger log = LoggerFactory.getLogger(DispatchCancelService.class);

    public DispatchCancelResult cancel(DispatchCancelCommand command) {

        log.info("[DispatchCancel] 요청 수신 - passengerId={}, dispatchId={}",
                command.getPassengerId(), command.getDispatchId());

        dispatchPassengerPermissionValidator.validate(command.getRole());

        Dispatch dispatch = queryPort.findById(command.getDispatchId());
        DispatchStatus before = dispatch.getDispatchStatus();

        dispatch.cancel();
        commandPort.save(dispatch);

        historyService.saveStatusChange(
                dispatch.getDispatchId(),
                DispatchDomainEventType.CANCELLED,
                before,
                dispatch.getDispatchStatus(),
                ChangedBy.PASSENGER
        );

        eventPort.publishCanceled(
                DispatchCanceledPayload.fromPassenger(dispatch)
        );

        log.info("[Cancel] 완료 - dispatchId={}", dispatch.getDispatchId());

        return DispatchCancelResult.builder()
                .dispatchId(dispatch.getDispatchId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .cancelledAt(dispatch.getCancelledAt())
                .build();
    }
}


