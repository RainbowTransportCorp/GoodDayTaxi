package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.application.port.out.core.view.PaymentIdentityView;
import com.gooddaytaxi.payment.domain.entity.Payment;
import com.gooddaytaxi.payment.domain.entity.Refund;
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

    Page<Refund> searchRefunds(String status, String reason, Boolean existRequest, UUID passeangerId, UUID driverId, UUID tripId, String method, Long minAmount, Long maxAmount, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);

    @Query("""
        select p.id as id,
               p.passengerId as passengerId,
               p.driverId as driverId,
               p.tripId as tripId,
               p.status as status
        from Payment p
        where p.id = :paymentId
    """)
    Optional<PaymentIdentityView> findIdentityById(UUID paymentId);
}
