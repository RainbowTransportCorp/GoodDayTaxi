package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentQueryPort {
    Optional<Payment> findByTripId(UUID tripId);

    Optional<Payment> findById(UUID paymentId);

    Page<Payment> searchPayments( String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay,  Pageable pageable);

    boolean existByTripIdAndNotStatusForCreate(UUID tripId);

    Payment findLastByTripIdAndStatusForCreate(UUID tripId);
}
