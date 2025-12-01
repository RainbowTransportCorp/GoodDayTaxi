package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.domain.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentQueryPort {
    Optional<Payment> findByTripId(UUID tripId);
}
