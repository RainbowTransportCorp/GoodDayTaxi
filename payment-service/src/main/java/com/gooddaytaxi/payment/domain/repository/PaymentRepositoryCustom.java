package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryCustom {
    Optional<PaymentAttempt> findFirstByPaymentIdOrderByAttemptNoDesc(UUID paymentId);
    Page<Payment> searchPayments(String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);

    Page<Refund> searchRefunds(String status, String reason, Boolean existRequest, UUID passeangerId, UUID driverId, UUID tripId, String method, Long minAmount, Long maxAmount, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);

}
