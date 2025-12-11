package com.gooddaytaxi.payment.infrastructure.outbox.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddaytaxi.payment.application.event.PaymentCompletePayload;
import com.gooddaytaxi.payment.application.event.RefundRequestCreatePayload;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventOutboxPort;
import com.gooddaytaxi.payment.application.port.out.event.PaymentEventCommandPort;
import com.gooddaytaxi.payment.infrastructure.outbox.publisher.PaymentBaseOutboxPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventCommandAdapter extends PaymentBaseOutboxPublisher implements PaymentEventCommandPort {

    private static final String AGGREGATE_TYPE = "Payment";
    private static final int VERSION = 1;

    public PaymentEventCommandAdapter(ObjectMapper objectMapper, PaymentEventOutboxPort outboxPort) {
        super(objectMapper, outboxPort);
    }

    @Override
    public void publishPaymentCompleted(PaymentCompletePayload payload) {
        publish(
                "PAYMENT_COMPLETE",
                "payment.complete",
                AGGREGATE_TYPE,
                payload.notificationOriginId(),
                payload.passeangerId().toString(),
                VERSION,
                payload
        );
    }

    @Override
    public void publishRefundRequestCreated(RefundRequestCreatePayload payload) {
        publish(
                "REFUND_REQUEST_CREATED",
                "refund.request.created",
                AGGREGATE_TYPE,
                payload.notificationOriginId(),
                payload.paymentId().toString(),
                VERSION,
                payload
        );
    }
}
