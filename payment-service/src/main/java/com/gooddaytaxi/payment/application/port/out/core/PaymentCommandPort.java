package com.gooddaytaxi.payment.application.port.out.core;

import com.gooddaytaxi.payment.domain.entity.Payment;

public interface PaymentCommandPort {
    void save(Payment payment);
}
