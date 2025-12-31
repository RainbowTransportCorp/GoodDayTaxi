package com.gooddaytaxi.dispatch.application.outbox;

import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;

import java.util.List;

/**
 * Outbox 패턴에서 이벤트 저장 및 상태 관리를 담당하는 Port.
 *
 * Application 계층은 이 Port를 통해 Outbox에 이벤트를 저장하고,
 * 전송 여부에 대한 상태만을 제어한다.
 */
public interface DispatchEventOutboxPort {

    /**
     * Outbox 이벤트를 저장하거나 상태 변경을 반영한다.
     */
    void save(DispatchEvent event);

    /**
     * 아직 전송되지 않은(PENDING) Outbox 이벤트를 조회한다.
     *
     * @param limit 한 번에 조회할 최대 이벤트 개수
     * @return 전송 대기 중인 Outbox 이벤트 목록
     */
    List<DispatchEvent> findPending(int limit);
}

