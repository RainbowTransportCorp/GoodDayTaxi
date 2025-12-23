package com.gooddaytaxi.payment.application.event;

import com.gooddaytaxi.payment.application.event.payload.RefundCompletedPayload;
import com.gooddaytaxi.payment.application.exception.PaymentErrorCode;
import com.gooddaytaxi.payment.application.exception.PaymentException;
import com.gooddaytaxi.payment.application.port.out.core.PaymentQueryPort;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventCommandPort;
import com.gooddaytaxi.payment.domain.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RefundCompletedEventListener {

    private final PaymentQueryPort paymentQueryPort;
    private final PaymentEventCommandPort eventCommandPort;


    // 트랜잭션 커밋 이후에 이벤트 처리
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RefundCompletedEvent event) {

        // 이 시점에는 DB commit 완료 -> refundId 존재
        Payment payment = paymentQueryPort.findById(event.paymentId())
                .orElseThrow(()-> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //이벤트 발행
        eventCommandPort.publishRefundCompleted(
                RefundCompletedPayload.from(payment, payment.getRefund(), event.notifierId())
        );
    }
}
