package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository;

import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchEventJpaRepository extends JpaRepository<DispatchEvent, UUID>, DispatchEventJpaRepositoryCustom {
}
