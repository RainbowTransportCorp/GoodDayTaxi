package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher.DispatchTimeoutOutboxPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchTimeoutService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final DispatchTimeoutOutboxPublisher eventPort;
    private final DispatchHistoryService historyService;

    private static final Logger log = LoggerFactory.getLogger(DispatchTimeoutService.class);

    public void runTimeoutCheck() {

        List<Dispatch> targets = queryPort.findTimeoutTargets(30);

        for (Dispatch dispatch : targets) {

            DispatchStatus before = dispatch.getDispatchStatus();

            dispatch.timeout();
            commandPort.save(dispatch);

            historyService.saveStatusChange(
                    dispatch.getDispatchId(),
                    DispatchDomainEventType.TIMEOUT,
                    before,
                    dispatch.getDispatchStatus(),
                    ChangedBy.SYSTEM
            );

            eventPort.publishTimeout(
                    new DispatchTimeoutPayload(
                            dispatch.getDispatchId(),
                            dispatch.getTimeoutAt()
                    )
            );

            log.info("[Timeout] 처리 - dispatchId={}", dispatch.getDispatchId());
        }
    }
}

