package com.gooddaytaxi.payment.application.event;

import java.util.UUID;

public record RefundCompletedEvent(
        UUID paymentId,
        UUID notifierId
) {

}
