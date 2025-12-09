package com.gooddaytaxi.dispatch.infrastructure.persistence.dispatch;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gooddaytaxi.dispatch.domain.model.entity.QDispatch.dispatch;

@Repository
@RequiredArgsConstructor
public class DispatchRepositoryImpl implements DispatchRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Dispatch> findAllByCondition(UUID passengerId) {
        return queryFactory
                .selectFrom(dispatch)
                .where(dispatch.createdBy.eq(passengerId))
                .orderBy(dispatch.requestCreatedAt.asc())
                .stream().toList();
    }

    @Override
    public List<Dispatch> findByStatus(DispatchStatus status) {
        return queryFactory
                .selectFrom(dispatch)
                .where(dispatch.dispatchStatus.eq(status))
                .orderBy(dispatch.requestCreatedAt.asc())
                .fetch();
    }

    @Override
    public Optional<Dispatch> findByDispatchId(UUID dispatchId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dispatch)
                        .where(dispatch.dispatchId.eq(dispatchId))
                        .fetchOne());
    }
}