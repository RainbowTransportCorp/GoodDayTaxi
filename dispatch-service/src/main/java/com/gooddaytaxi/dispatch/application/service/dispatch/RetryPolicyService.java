package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryPolicyService {

    private static final int MAX_RETRY = 3;

    public boolean isRetryLimitExceeded(Dispatch dispatch) {
        return dispatch.getReassignAttemptCount() >= MAX_RETRY;
    }
    public int maxRetry() {
        return MAX_RETRY;
    }
}
