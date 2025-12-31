package com.gooddaytaxi.payment.infrastructure.outbox.persistence;

import com.gooddaytaxi.payment.infrastructure.outbox.entity.PaymentEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentEventRepository extends JpaRepository<PaymentEvent, UUID>, PaymentEventRepositoryCustom {
    @Query("""
    SELECT e FROM PaymentEvent e
    WHERE e.status = 'PENDING'
    ORDER BY e.createdAt ASC
    """)
    List<PaymentEvent> findPending(Pageable pageable);

    @Modifying
    @Query("""
        UPDATE PaymentEvent e
        SET e.status = 'SENT',
            e.publishedAt = CURRENT_TIMESTAMP
        WHERE e.id = :eventId
            AND e.status = 'PENDING'
    """)// 마지막줄은 중복 업데이트 방지용
    int updateStatusPublished(@Param("eventId") UUID eventId);
}
