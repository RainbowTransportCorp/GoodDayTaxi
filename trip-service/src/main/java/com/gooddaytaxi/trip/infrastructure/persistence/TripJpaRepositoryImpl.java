package com.gooddaytaxi.trip.infrastructure.persistence;

import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TripJpaRepositoryImpl implements TripJpaRepositoryCustom {

    private final EntityManager em;

    /**
     * 승객은 운행중 상태만 조회가 가능
     *
     * @param passengerId 요청 승객 식별자
     * @return 운행정보
     */
    @Override
    public Optional<Trip> findActiveByPassengerId(UUID passengerId) {
        return em.createQuery("""
                    select t
                    from Trip t
                    where t.passengerId = :passengerId
                    and t.status in (:statuses)
                    order by t.startTime desc
                """, Trip.class)
            .setParameter("passengerId", passengerId)
            .setParameter("statuses", List.of(TripStatus.READY, TripStatus.STARTED))
            .setMaxResults(1)
            .getResultStream()
            .findFirst();
    }

    @Override
    public Optional<Trip> findByTripIdAndPassengerId(UUID tripId, UUID passengerId) {
        return em.createQuery("""
            select t
            from Trip t
            where t.tripId = :tripId
              and t.passengerId = :passengerId
        """, Trip.class)
            .setParameter("tripId", tripId)
            .setParameter("passengerId", passengerId)
            .getResultStream()
            .findFirst();
    }

    @Override
    public Optional<Trip> findByTripIdAndDriverId(UUID tripId, UUID driverId) {
        return em.createQuery("""
            select t
            from Trip t
            where t.tripId = :tripId
              and t.driverId = :driverId
        """, Trip.class)
            .setParameter("tripId", tripId)
            .setParameter("driverId", driverId)
            .getResultStream()
            .findFirst();
    }

    @Override
    public List<Trip> findAllByPassengerId(UUID passengerId) {
        return em.createQuery("""
            select t
            from Trip t
            where t.passengerId = :passengerId
            order by t.createdAt desc
        """, Trip.class)
            .setParameter("passengerId", passengerId)
            .getResultList();
    }

    @Override
    public List<Trip> findAllByDriverId(UUID driverId) {
        return em.createQuery("""
            select t
            from Trip t
            where t.driverId = :driverId
            order by t.createdAt desc
        """, Trip.class)
            .setParameter("driverId", driverId)
            .getResultList();
    }


    /**
     * 기사는 운행 대기와 운행중 상태 모두 조회 가능
     *
     * @param driverId 요청 기사 식별자
     * @return 운행정보
     */
    @Override
    public Optional<Trip> findActiveByDriverId(UUID driverId) {
        return em.createQuery("""
                    select t
                    from Trip t
                    where t.driverId = :driverId
                      and t.status in (:statuses)
                    order by t.createdAt desc
                """, Trip.class)
            .setParameter("driverId", driverId)
            .setParameter("statuses", List.of(TripStatus.READY, TripStatus.STARTED))
            .setMaxResults(1)
            .getResultStream()
            .findFirst();
    }

}
