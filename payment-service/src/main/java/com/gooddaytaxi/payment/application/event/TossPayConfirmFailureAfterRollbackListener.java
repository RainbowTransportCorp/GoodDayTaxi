package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.application.service.PaymentFailureRecorder;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPayConfirmFailureAfterRollbackListener {

    private final PaymentFailureRecorder failureRecorder;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handle(TossPayConfirmFailedAfterRollbackEvent event) {
        log.warn("[AFTER_ROLLBACK] confirm failed. paymentId={}, attemptNo={}, status={}",
                event.paymentId(), event.attemptNo(), event.error().status());

        failureRecorder.recordConfirmFailureAfterRollback(event.paymentId(), new PaymentAttempt(event.paymentKey(), event.idempotencyKey(), event.attemptNo()),event.error(), event.command());
    }
}
