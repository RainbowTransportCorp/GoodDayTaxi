package com.gooddaytaxi.dispatch.application.usecase.timeout;

import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchTripRequestMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class TripRequestMonitorScheduler {

    private final DispatchTripRequestMonitor monitor;

    @Scheduled(fixedDelay = 5000)
    public void checkTripRequestResponses() {
        monitor.check();
    }
}