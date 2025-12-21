package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository;

import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import java.util.UUID;
import javax.swing.Spring;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchEventJpaRepository extends JpaRepository<DispatchEvent, UUID>,
    DispatchEventJpaRepositoryCustom {

    /*
    Spring Data JPA가 메서드 이름을 파싱해서 EXISTS 쿼리를 자동 생성한다. (구현체 불필요)
    aggregateId + eventType 조건으로 "해당 이벤트가 기록된 적 있는지"만 빠르게 확인하는 용도
     */
    boolean existsByAggregateIdAndEventType(
        UUID aggregateId,
        String eventType
    );
}
