package com.gooddaytaxi.dispatch.infrastructure.persistence.assignment;

import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.gooddaytaxi.dispatch.domain.model.entity.QDispatchAssignmentLog.dispatchAssignmentLog;

@Repository
@RequiredArgsConstructor
public class DispatchAssignmentLogRepositoryImpl implements DispatchAssignmentLogRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<DispatchAssignmentLog> findLatest(UUID dispatchId, UUID driverId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dispatchAssignmentLog)
                        .where(
                                dispatchAssignmentLog.dispatchId.eq(dispatchId),
                                dispatchAssignmentLog.candidateDriverId.eq(driverId)
                        )
                        .orderBy(dispatchAssignmentLog.attemptedAt.desc())
                        .limit(1)
                        .fetchOne()
        );
    }
}

