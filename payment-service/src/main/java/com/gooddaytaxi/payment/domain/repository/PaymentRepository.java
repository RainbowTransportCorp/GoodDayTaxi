package com.gooddaytaxi.payment.domain.repository;

import com.gooddaytaxi.payment.domain.entity.Payment;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> , PaymentRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("select p from Payment p where p.id = :paymentId")
    Optional<Payment> findByIdWithLock(@Param("paymentId") UUID paymentId);

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("""
    SELECT p
    FROM Payment p
    WHERE p.tripId = :tripId
      AND p.status = 'IN_PROCESS'
    ORDER BY p.createdAt DESC""")
    Payment findLastByTripIdAndStatusForCreateWithLock(@Param("tripId") UUID tripId);

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
