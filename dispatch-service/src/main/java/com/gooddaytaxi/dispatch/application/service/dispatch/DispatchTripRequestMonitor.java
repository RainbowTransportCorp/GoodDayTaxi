package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchTripReadyErrorPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchTripReadyErrorCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchEventQueryPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchTripRequestMonitor {

    /**
     * Trip 생성 요청 이후,
     * Trip 서비스가 "운행 대기(READY)" 상태로 정상 진입했는지
     * 확인 가능한 최대 대기 시간
     */
    private static final long TRIP_READY_TIMEOUT_SECONDS = 30;

    private final DispatchQueryPort dispatchQueryPort;
    private final DispatchEventQueryPort dispatchEventQueryPort;
    private final DispatchTripReadyErrorCommandPort errorEventPort;

    /**
     * Trip 생성 요청(TRIP_REQUEST) 이후
     * 일정 시간이 지났음에도 Trip READY 상태가 확인되지 않은 Dispatch를 감시한다.
     *
     * ❗ Dispatch 상태 전이는 수행하지 않는다.
     * ❗ 비정상 흐름에 대한 에러 이벤트만 발행한다.
     */
    public void check() {

        List<Dispatch> targets =
            dispatchQueryPort.findByStatus(DispatchStatus.TRIP_REQUEST);

        if (targets.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Dispatch dispatch : targets) {
            try {
                monitorSingleDispatch(dispatch, now);
            } catch (Exception e) {
                log.error(
                    "[TRIP-REQUEST-MONITOR-ERROR] dispatchId={} err={}",
                    dispatch.getDispatchId(),
                    e.getMessage(),
                    e
                );
            }
        }
    }

    private void monitorSingleDispatch(Dispatch dispatch, LocalDateTime now) {

        // 1️⃣ Trip READY 여부 확인
        // - Trip DB를 직접 조회하지 않는다.
        // - Dispatch Outbox(Event) 기준으로
        //   "Trip READY에 해당하는 이벤트가 수신되었는지"만 판단한다.
        if (isTripReadyConfirmed(dispatch.getDispatchId())) {
            return;
        }

        // 2️⃣ TRIP_REQUEST 이후 경과 시간 계산
        if (!isTimeoutExceeded(dispatch, now)) {
            return;
        }

        // 3️⃣ 에러 이벤트 발행 (상태 전이 ❌)
        errorEventPort.publish(
            DispatchTripReadyErrorPayload.tripReadyTimeout(
                dispatch.getDispatchId(),
                dispatch.getPassengerId(),
                Duration.between(dispatch.getUpdatedAt(), now).getSeconds()
            )
        );

        log.warn(
            "[TRIP-REQUEST-TIMEOUT] Trip READY 미확인 - dispatchId={} elapsed={}s",
            dispatch.getDispatchId(),
            Duration.between(dispatch.getUpdatedAt(), now).getSeconds()
        );
    }

    /**
     * Dispatch 기준으로 Trip READY가 확인되었는지 여부
     *
     * - Trip 서비스 상태를 조회하지 않는다.
     * - Dispatch가 수신/기록한 이벤트 기준으로 판단한다.
     */
    private boolean isTripReadyConfirmed(UUID dispatchId) {
        return dispatchEventQueryPort.existsTripCreated(dispatchId);
    }

    /**
     * Trip 생성 요청 이후 허용된 대기 시간을 초과했는지 여부
     */
    private boolean isTimeoutExceeded(Dispatch dispatch, LocalDateTime now) {
        long elapsedSeconds =
            Duration.between(dispatch.getUpdatedAt(), now).getSeconds();

        return elapsedSeconds >= TRIP_READY_TIMEOUT_SECONDS;
    }

    /**
     * Trip READY 상태로 전이된 Dispatch는 모니터링 대상에서 제외된다.
     *
     * 현재는 DB 상태 기반으로만 판단하므로 no-op.
     * (필요 시 캐시 / 플래그 기반 확장 가능)
     */
    public void clear(UUID dispatchId) {
        // no-op
    }
}

