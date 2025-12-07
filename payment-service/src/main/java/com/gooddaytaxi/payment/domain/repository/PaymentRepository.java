package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> , PaymentRepositoryCustom {
    Optional<Payment> findByTripId(UUID tripId);
    Page<Payment> searchPayments(String method, String status, UUID passeangerId, UUID driverId, UUID tripId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);

    @Query("""
    SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
    FROM Payment p
    WHERE p.tripId = :tripId
      AND p.status IN ('PENDING', 'IN_PROCESS', 'FAILED', 'COMPLETED')""")
    boolean existByTripIdAndNotStatusForCreate(UUID tripId);

    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.tripId = :tripId
      AND p.status IN ('PENDING', 'IN_PROCESS', 'FAILED', 'COMPLETED')
    ORDER BY p.createdAt DESC""")
    Payment findLastByTripIdAndStatusForCreate(@Param("tripId") UUID tripId);
}
