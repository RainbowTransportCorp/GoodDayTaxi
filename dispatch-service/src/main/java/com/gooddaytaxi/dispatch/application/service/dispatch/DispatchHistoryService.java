package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchDomainEventType;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispatchHistoryService {

    private final DispatchHistoryCommandPort historyPort;

    public void saveStatusChange(UUID dispatchId,
                                 DispatchDomainEventType eventType,
                                 DispatchStatus from,
                                 DispatchStatus to,
                                 ChangedBy changedBy) {

        historyPort.save(
                DispatchHistory.recordStatusChange(
                        dispatchId,
                        eventType.name(),
                        from,
                        to,
                        changedBy,
                        null
                )
        );
    }
}

