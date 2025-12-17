package com.gooddaytaxi.dispatch.infrastructure.persistence.assignment;

import com.gooddaytaxi.dispatch.domain.model.entity.Dispatch;
import com.gooddaytaxi.dispatch.domain.model.entity.DispatchAssignmentLog;
import com.gooddaytaxi.dispatch.domain.model.enums.DispatchStatus;
import com.gooddaytaxi.dispatch.domain.repository.DispatchAssignmentLogRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gooddaytaxi.dispatch.domain.model.entity.QDispatch.dispatch;
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

    /**
     * 특정 기사에게 배차 제안이 이루어진 배차 중,
     * 아직 배차가 확정되지 않은(ASSIGNING) 건을 조회한다.
     *
     * @param driverId 배차 제안을 받은 기사 ID
     * @return 해당 기사에게 제안된 미확정 배차 목록
     */

    @Override
    public List<Dispatch> findPendingByCandidateDriver(UUID driverId) {
        return queryFactory
                .select(dispatch)
                .from(dispatch)
                .join(dispatchAssignmentLog)
                .on(dispatchAssignmentLog.dispatchId.eq(dispatch.dispatchId))
                .where(
                        dispatchAssignmentLog.candidateDriverId.eq(driverId),
                        dispatch.dispatchStatus.eq(DispatchStatus.ASSIGNING)
                )
                .fetch();
    }
  
    @Override
    public List<UUID> findAllDriverIdsByDispatchId(UUID dispatchId) {
        return queryFactory
                .select(dispatchAssignmentLog.candidateDriverId)
                .from(dispatchAssignmentLog)
                .where(dispatchAssignmentLog.dispatchId.eq(dispatchId))
                .fetch();
    }

}

