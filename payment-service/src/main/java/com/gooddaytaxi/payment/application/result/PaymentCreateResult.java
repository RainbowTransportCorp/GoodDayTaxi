package com.gooddaytaxi.payment.application.result;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class PaymentCreateResult {
    private UUID paymentId;
    private String method;
    private Long amount;
    private Long passengerId;
    private Long driverId;
    private UUID tripId;
    public PaymentCreateResult(UUID paymentId, String method, Long amount, Long passengerId, Long driverId, UUID tripId) {
        this.paymentId = paymentId;
        this.method = method;
        this.amount = amount;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.tripId = tripId;
    }
}
