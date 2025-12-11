package com.gooddaytaxi.payment.application.port.out.event;

import com.gooddaytaxi.payment.application.event.PaymentCompletePayload;
import com.gooddaytaxi.payment.application.event.RefundRequestCreatePayload;

public interface PaymentEventCommandPort {
    void publishPaymentCompleted(PaymentCompletePayload payload);

    void publishRefundRequestCreated(RefundRequestCreatePayload payload);
}
