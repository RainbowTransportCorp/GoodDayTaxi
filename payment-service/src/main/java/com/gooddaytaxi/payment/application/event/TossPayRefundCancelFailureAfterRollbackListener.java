package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.application.service.PaymentFailureRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPayRefundCancelFailureAfterRollbackListener {

    private final PaymentFailureRecorder failureRecorder; // 너가 쓰던 recorder

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handle(TossPayRefundCancelFailedAfterRollbackEvent event) {
        log.warn("[AFTER_ROLLBACK] refund cancel failed. paymentId={}, status={}",
                event.paymentId(), event.error().status());

        failureRecorder.recordCancelFailureAfterRollback(
                event.paymentId(),
                event.refund(),
                event.error()
        );
    }
}
