package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.adapter;

import com.gooddaytaxi.dispatch.application.outbox.DispatchEventOutboxPort;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository.DispatchEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchEventOutboxAdapter implements DispatchEventOutboxPort {

    private final DispatchEventJpaRepository repository;

    /**
     * Outbox 이벤트 엔티티를 저장하거나
     * 상태 변경을 반영한다.
     */
    @Override
    public void save(DispatchEvent event) {
        repository.save(event);
    }

    /**
     * 아직 전송되지 않은(PENDING) Outbox 이벤트를 조회한다.
     *
     * @param limit 한 번에 조회할 최대 이벤트 개수
     * @return 전송 대기 중인 Outbox 이벤트 엔티티 목록
     */
    @Override
    public List<DispatchEvent> findPending(int limit) {
        return repository.findPending(limit);
    }
}
