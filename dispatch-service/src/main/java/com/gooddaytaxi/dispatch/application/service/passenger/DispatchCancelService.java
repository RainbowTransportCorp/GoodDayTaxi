package com.gooddaytaxi.dispatch.application.service.passenger;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchCanceledPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCanceledCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchHistoryService;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelCommand;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelPermissionValidator;
import com.gooddaytaxi.dispatch.application.usecase.cancel.DispatchCancelResult;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchCancelService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;

    private final DispatchCanceledCommandPort eventPort;
    private final DispatchHistoryService historyService;

    private final DispatchCancelPermissionValidator permissionValidator;

    public DispatchCancelResult cancel(DispatchCancelCommand command) {

        log.info("[DispatchCancel] 요청 수신 - passengerId={}, dispatchId={}",
                command.getPassengerId(), command.getDispatchId());

        Dispatch dispatch = queryPort.findById(command.getDispatchId());

        permissionValidator.validate(command.getRole(), command.getPassengerId(), dispatch.getPassengerId());

        // 취소 전 상태 보관 (정책 판단용)
        DispatchStatus before = dispatch.getDispatchStatus();

        // 2. 도메인 처리
        dispatch.cancelByPassenger();
        commandPort.save(dispatch);

        // 3. 기사 수락 후 취소만 이벤트 발행
        if (before == DispatchStatus.ACCEPTED) {
            eventPort.publishCanceled(
                    DispatchCanceledPayload.fromPassenger(dispatch)
            );
        }

        // 4. 히스토리 (실패해도 비즈니스 흐름 유지)
        try {
            historyService.saveStatusChange(
                    dispatch.getDispatchId(),
                    HistoryEventType.USER_CANCELED,
                    before,
                    dispatch.getDispatchStatus(),
                    ChangedBy.PASSENGER,
                    null
            );
        } catch (Exception e) {
            log.error("[DispatchCancel] 히스토리 기록 실패 - dispatchId={}, error={}",
                    dispatch.getDispatchId(), e.getMessage());
        }

        log.info("[DispatchCancel] 완료 - dispatchId={}", dispatch.getDispatchId());

        return DispatchCancelResult.builder()
                .dispatchId(dispatch.getDispatchId())
                .dispatchStatus(dispatch.getDispatchStatus())
                .canceledAt(dispatch.getCanceledAt())
                .build();
    }
}
