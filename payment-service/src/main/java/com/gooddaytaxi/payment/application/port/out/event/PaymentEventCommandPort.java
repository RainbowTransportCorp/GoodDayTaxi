package com.gooddaytaxi.payment.application.port.out.event;

import com.gooddaytaxi.payment.application.event.payload.PaymentCompletePayload;
import com.gooddaytaxi.payment.application.event.payload.RefundCompletedPayload;
import com.gooddaytaxi.payment.application.event.payload.RefundRequestCreatePayload;
import com.gooddaytaxi.payment.application.event.payload.RefundRequestRejectedPayload;
import com.gooddaytaxi.payment.application.event.payload.RefundSettlementPayload;

public interface PaymentEventCommandPort {
    void publishPaymentCompleted(PaymentCompletePayload payload);

    void publishRefundRequestCreated(RefundRequestCreatePayload payload);

    void publishRefundRequestRejected(RefundRequestRejectedPayload payload);

    void publishRefundSettlementCreated(RefundSettlementPayload payload);

    void publishRefundCompleted(RefundCompletedPayload payload);
}
