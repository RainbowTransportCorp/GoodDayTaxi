package com.gooddaytaxi.payment.application.port.out.core;

import com.gooddaytaxi.payment.domain.entity.PaymentAttempt;
import com.gooddaytaxi.payment.domain.repository.PaymentIdentityView;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentQueryPort {
    Optional<Payment> findById(UUID paymentId);

    Optional<PaymentAttempt> findLastAttemptByPaymentId(UUID paymentId);


    Page<Payment> searchPayments( String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay,  Pageable pageable);

    boolean existByTripIdAndNotStatusForCreate(UUID tripId);

    Payment findLastByTripIdAndStatusForCreate(UUID tripId);

    Page<Refund> searchRefunds(String status, String reason, Boolean existRequest, UUID passeangerId, UUID driverId, UUID tripId, String method, Long minAmount, Long maxAmount, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    Optional<PaymentIdentityView> findIdentityViewById(UUID paymentId);
}
