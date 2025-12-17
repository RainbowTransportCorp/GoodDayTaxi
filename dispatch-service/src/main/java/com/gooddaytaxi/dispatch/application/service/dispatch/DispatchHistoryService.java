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

    /**
     * 배차 상태가 변경될 때마다 기록되는 history의 생성자
     * @param dispatchId 배차 식별자
     * @param eventType 현재 변경된 타입
     * @param from 변경 전 배차 상태
     * @param to 변경 후 배차 상태
     * @param changedBy 변경한 이의 권한정보
     * @param reason 변경 사유 (nullable 허용)
     */
    public void saveStatusChange(
            UUID dispatchId,
            HistoryEventType eventType,
            DispatchStatus from,
            DispatchStatus to,
            ChangedBy changedBy,
            String reason
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

