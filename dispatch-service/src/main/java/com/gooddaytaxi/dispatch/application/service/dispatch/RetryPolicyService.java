package com.gooddaytaxi.dispatch.application.service.dispatch;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetryPolicyService {

    private static final int MAX_RETRY = 3;

    public boolean isRetryLimitExceeded(Dispatch dispatch) {
        return dispatch.getReassignAttemptCount() >= MAX_RETRY;
    }
}
