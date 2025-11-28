package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.domain.entity.Payment;

public interface PaymentCommandPort {
    Payment save(Payment payment);
}
