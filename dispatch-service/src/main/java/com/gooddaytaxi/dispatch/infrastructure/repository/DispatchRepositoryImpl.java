package com.gooddaytaxi.dispatch.infrastructure.repository;

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
    public List<Dispatch> findAllByCondition() {
        return List.of();
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
    public Optional<Dispatch> findByDispatchId(UUID id) {
        return Optional.empty();
    }
}