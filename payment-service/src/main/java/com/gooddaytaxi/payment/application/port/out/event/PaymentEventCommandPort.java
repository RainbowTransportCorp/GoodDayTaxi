package com.gooddaytaxi.payment.application.port.out.event;

import com.gooddaytaxi.payment.application.event.PaymentCompletePayload;

public interface PaymentEventCommandPort {
    void publishPaymentCompleted(PaymentCompletePayload payload);
}
