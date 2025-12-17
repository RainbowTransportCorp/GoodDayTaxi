package com.gooddaytaxi.dispatch.application.outbox;

import java.util.List;
import java.util.UUID;

/**
 * Outbox 패턴에서 이벤트 저장 및 상태 관리를 담당하는 Port.
 *
 * Application 계층은 이 Port를 통해 Outbox에 이벤트를 저장하고,
 * 전송 여부에 대한 상태만을 제어한다.
 */
public interface DispatchEventOutboxPort {

    /**
     * Outbox에 이벤트를 저장
     * @param event 저장할 Outbox 이벤트 모델
     */
    void save(OutboxEventModel event);

    /**
     * 아직 전송되지 않은 이벤트를 조회
     * @param limit 한 번에 조회할 최대 이벤트 개수
     * @return 전송 대기 중인 Outbox 이벤트 목록
     */
    List<OutboxEventModel> findPending(int limit);

    /**
     * 이벤트를 전송 완료 상태로 변경
     * @param eventId 상태를 변경할 이벤트 식별자
     */
    void markPublished(UUID eventId);

}
