package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.port.out.command.DispatchHistoryCommandPort;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchHistory;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispatchHistoryService {

    private final DispatchHistoryCommandPort historyPort;

    public void saveStatusChange(
            UUID dispatchId,
            HistoryEventType eventType,
            DispatchStatus from,
            DispatchStatus to,
            ChangedBy changedBy,
            String reason   // ← nullable 허용
    ) {
        historyPort.save(
                DispatchHistory.recordStatusChange(
                        dispatchId,
                        eventType.name(),
                        from,
                        to,
                        changedBy,
                        reason
                )
        );
    }
}

