package com.gooddaytaxi.dispatch.application.usecase.timeout;

import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchTripRequestMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component //에러 이벤트 컴포넌트는 아직 trip에서 반환 (ready) 이벤트를 발행하지 않으므로 주석처리
@RequiredArgsConstructor
public class TripRequestMonitorScheduler {

    private final DispatchTripRequestMonitor monitor;

    @Scheduled(fixedDelay = 5000)
    public void checkTripRequestResponses() {
        monitor.check();
    }
}