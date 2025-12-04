package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PaymentRepositoryCustom {
    Page<Payment> searchPayments(String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);
}
