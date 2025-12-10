package com.gooddaytaxi.dispatch.application.scheduler;

import com.gooddaytaxi.dispatch.application.event.payload.DispatchTimeoutPayload;
import com.gooddaytaxi.dispatch.application.port.out.command.DispatchCommandPort;
import com.gooddaytaxi.dispatch.application.port.out.query.DispatchQueryPort;
import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.publisher.DispatchTimeoutOutboxPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchTimeoutChecker {

    private final DispatchQueryPort queryPort;
    private final DispatchCommandPort commandPort;
    private final DispatchTimeoutOutboxPublisher publisher;

    @Scheduled(fixedDelay = 3000) //3초마다 스케줄러 실행
    public void checkTimeouts() {

        log.debug("[TIMEOUT-CHECKER] START - 타임아웃 대상 조회 시작");

        List<Dispatch> targets;
        try {
            targets = queryPort.findTimeoutTargets(60); //배차 시도 후 1분이 지나면 타임아웃
        } catch (Exception e) {
            log.error("[TIMEOUT-CHECKER-ERROR] 타임아웃 대상 조회 실패: {}", e.getMessage(), e);
            return;
        }

        log.info("[TIMEOUT-CHECKER] 타임아웃 대상 수: {}", targets.size());

        for (Dispatch dispatch : targets) {

            log.info("[TIMEOUT-CHECKER] 처리 시작 - dispatchId={}", dispatch.getDispatchId());

            try {
                // 1. 도메인 상태 변경
                dispatch.timeout();
                log.debug("[TIMEOUT-CHECKER] dispatch.timeout() 완료 - dispatchId={}", dispatch.getDispatchId());

                // 2. 상태 저장
                commandPort.save(dispatch);
                log.debug("[TIMEOUT-CHECKER] dispatch 저장 완료 - dispatchId={}", dispatch.getDispatchId());

                // 3. payload 생성
                DispatchTimeoutPayload payload = new DispatchTimeoutPayload(
                        dispatch.getDispatchId(),
                        dispatch.getTimeoutAt()
                );
                log.debug("[TIMEOUT-CHECKER] Payload 생성 완료 - dispatchId={}", dispatch.getDispatchId());

                // 4. outbox에 저장
                publisher.publishTimeout(payload);
                log.info("[TIMEOUT-CHECKER] OUTBOX 저장 완료 - dispatchId={}", dispatch.getDispatchId());

            } catch (Exception e) {
                log.error("[TIMEOUT-CHECKER-ERROR] 타임아웃 처리 실패 - dispatchId={} error={}",
                        dispatch.getDispatchId(), e.getMessage(), e);
            }
        }

        log.debug("[TIMEOUT-CHECKER] END - 실행 종료");
    }
}
