package com.gooddaytaxi.trip.infrastructure.messaging.outbox.repository;

import com.gooddaytaxi.trip.domain.model.enums.TripEventStatus;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.entity.TripEventOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripEventOutboxJpaRepository extends JpaRepository<TripEventOutbox, UUID> {

    List<TripEventOutbox> findByEventStatusOrderByCreatedAt(
            TripEventStatus status,
            Pageable pageable
    );

}
