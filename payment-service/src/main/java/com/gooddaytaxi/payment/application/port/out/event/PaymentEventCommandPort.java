package com.gooddaytaxi.payment.application.port.out.event;

import com.gooddaytaxi.payment.application.event.PaymentCompletePayload;
import com.gooddaytaxi.payment.application.event.RefundCompletedPayload;
import com.gooddaytaxi.payment.application.event.RefundRequestCreatePayload;
import com.gooddaytaxi.payment.application.event.RefundRequestRejectedPayload;
import com.gooddaytaxi.payment.application.event.RefundSettlementCreatedPayload;

public interface PaymentEventCommandPort {
    void publishPaymentCompleted(PaymentCompletePayload payload);

    void publishRefundRequestCreated(RefundRequestCreatePayload payload);

    void publishRefundRequestRejected(RefundRequestRejectedPayload payload);

    void publishRefundSettlementCreated(RefundSettlementCreatedPayload payload);

    void publishRefundCompleted(RefundCompletedPayload payload);
}
