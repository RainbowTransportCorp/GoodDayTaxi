package com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.repository;

import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.EventStatus;
import com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.DispatchEvent;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.gooddaytaxi.dispatch.infrastructure.messaging.outbox.entity.QDispatchEvent.dispatchEvent;

@RequiredArgsConstructor
public class DispatchEventJpaRepositoryImpl implements DispatchEventJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DispatchEvent> findPending(int limit) {
        return queryFactory
                .selectFrom(dispatchEvent)
                .where(dispatchEvent.eventStatus.eq(EventStatus.PENDING))
                .orderBy(dispatchEvent.createdAt.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public void updateStatusPublished(UUID eventId) {
        queryFactory
                .update(dispatchEvent)
                .set(dispatchEvent.eventStatus, EventStatus.SENT)
                .set(dispatchEvent.publishedAt, LocalDateTime.now())
                .where(dispatchEvent.eventId.eq(eventId))
                .execute();
    }
}
