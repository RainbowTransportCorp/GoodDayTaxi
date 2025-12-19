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

    /**
     * 특정 승객의 전체 배차 조회 (상태가 timeout인 경우 제외)
     * @param passengerId 요청 승객의 식별자
     * @return 특정 승객의 모든 배차 리스트
     */
    @Override
    public List<Dispatch> findAllByPassengerId(UUID passengerId) {
        return queryFactory
                .selectFrom(dispatch)
                .where(
                        dispatch.passengerId.eq(passengerId),
                        dispatch.dispatchStatus.ne(DispatchStatus.TIMEOUT)
                )
                .orderBy(dispatch.requestCreatedAt.asc())
                .fetch();
    }


    /**
     * 특정 승객의 특정 배차의 조회
     * @param dispatchId 요청한 특정 배차 식별자
     * @param passengerId 요청한 특정 승객의 식별자
     * @return 요청한 배차의 정보
     */
    @Override
    public Optional<Dispatch> findByIdAndPassengerId(UUID dispatchId, UUID passengerId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dispatch)
                        .where(
                                dispatch.dispatchId.eq(dispatchId),
                                dispatch.passengerId.eq(passengerId),
                                dispatch.dispatchStatus.ne(DispatchStatus.TIMEOUT)
                        )
                        .fetchOne()
        );
    }


    /**
     * 특정 배차 식별자를 가진 배차 정보를 조회
     * @param dispatchId 특정 배차 식별자
     * @return 식별자에 맞는 배차 정보
     */
    @Override
    public Optional<Dispatch> findByDispatchId(UUID dispatchId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(dispatch)
                        .where(dispatch.dispatchId.eq(dispatchId))
                        .fetchOne());
    }


    @Override
    public List<Dispatch> findTimeoutCandidates() {
        return queryFactory
                .selectFrom(dispatch)
                .where(dispatch.dispatchStatus
                        .in(DispatchStatus.ASSIGNING,
                                DispatchStatus.ASSIGNED
                        ))
                .fetch();
    }

    @Override
    public List<Dispatch> findByStatus(DispatchStatus status) {
        return queryFactory
                .selectFrom(dispatch)
                .where(dispatch.dispatchStatus.eq(status))
                .fetch();
    }

}