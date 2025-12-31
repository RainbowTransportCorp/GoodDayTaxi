package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.event.payload.TripCreateRequestPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.command.TripCreateRequestCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.exception.dispatch.DispatchInvalidStateException;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.ChangedBy;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.model.enums.HistoryEventType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispatchTripRequestService {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;
    private final DispatchHistoryService historyService;
    private final TripCreateRequestCommandPort tripCreateRequestCommandPort;

    /**
     * 배차 수락 이후 Trip 생성(운행 대기 상태)을 요청한다.
     * Dispatch 상태를 TRIP_REQUEST로 전이시키고,
     * Trip 서비스에 운행 생성 요청 이벤트를 발행한다.
     */
    @Transactional
    public void requestTrip(UUID dispatchId) {

        Dispatch dispatch = queryPort.findById(dispatchId);
        DispatchStatus before = dispatch.getDispatchStatus();

        try {
            // ===== 상태 전이 (도메인 규칙 위임) =====
            dispatch.markTripRequest();
            commandPort.save(dispatch);

            // ===== 히스토리 기록 =====
            historyService.saveStatusChange(
                dispatch.getDispatchId(),
                HistoryEventType.STATUS_CHANGED,
                before,
                dispatch.getDispatchStatus(),
                ChangedBy.SYSTEM,
                "Trip 생성 요청 전송"
            );

            // ===== Trip 생성 요청 이벤트 발행 =====
            tripCreateRequestCommandPort.publishTripCreateRequest(
                TripCreateRequestPayload.from(dispatch)
            );

            log.info(
                "[TRIP-REQUEST] TripCreateRequest 발행 완료 - dispatchId={} status={}",
                dispatch.getDispatchId(),
                dispatch.getDispatchStatus()
            );

        } catch (DispatchInvalidStateException e) {
            // 정책적 무시 (중복 호출 / 이미 처리됨 등)
            log.warn(
                "[TRIP-REQUEST-SKIP] invalid transition - dispatchId={} status={}",
                dispatchId,
                before
            );
        }
    }

}

