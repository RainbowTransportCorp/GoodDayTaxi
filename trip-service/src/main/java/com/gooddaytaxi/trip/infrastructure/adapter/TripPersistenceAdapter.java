package com.gooddaytaxi.trip.infrastructure.adapter;

import com.gooddaytaxi.trip.application.port.out.CreateTripPort;
import com.gooddaytaxi.trip.application.port.out.ExistsTripByDispatchIdPort;
import com.gooddaytaxi.trip.application.port.out.LoadTripsByPassengerPort;
import com.gooddaytaxi.trip.application.port.out.UpdateTripPort;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.infrastructure.persistence.TripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TripPersistenceAdapter implements CreateTripPort, UpdateTripPort, LoadTripsByPassengerPort, ExistsTripByDispatchIdPort {

    private final TripJpaRepository tripJpaRepository;


    @Override
    public Trip createTrip(Trip trip) {
        return tripJpaRepository.save(trip);

    }

    @Override
    public Trip updateTrip(Trip trip) {
        return tripJpaRepository.save(trip);
    }


    @Override
    public PassengerTripsPage loadTripsByPassengerId(UUID passengerId, int page, int size) {

        // page 또는 size가 이상한 값으로 들어올 경우 방어 처리
        int safePage = Math.max(page, 0);   // 0 미만이면 0으로 강제
        int safeSize = size <= 0 ? 10 : size; // size가 0 이하이면 기본값 10 적용

        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<Trip> tripPage = tripJpaRepository.findByPassengerId(passengerId, pageable);

        return new PassengerTripsPage(
                tripPage.getContent(),       // 현재 페이지 데이터
                tripPage.getTotalElements()  // 전체 데이터 수
        );
    }


    @Override
    public boolean existsByDispatchId(UUID dispatchId) {
        return tripJpaRepository.existsByDispatchId(dispatchId);
    }



}
