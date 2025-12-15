package com.gooddaytaxi.dispatch.application.usecase.timeout;

import com.gooddaytaxi.dispatch.application.service.dispatch.DispatchTimeoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DispatchTimeoutChecker {

    private final DispatchTimeoutService timeoutService;

    @Scheduled(fixedDelay = 3000)
    public void checkTimeouts() {
        timeoutService.runTimeoutCheck();
    }
}


