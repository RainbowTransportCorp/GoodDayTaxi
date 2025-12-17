package com.gooddaytaxi.trip.application.service;


import com.gooddaytaxi.trip.application.command.EndTripCommand;
import com.gooddaytaxi.trip.application.command.StartTripCommand;
import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.port.out.*;
import com.gooddaytaxi.trip.application.result.*;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import com.gooddaytaxi.trip.infrastructure.messaging.outbox.TripOutboxAppender;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {


    private final CreateTripPort createTripPort;
    private final LoadTripsPort loadTripsPort;
    private final LoadTripByIdPort loadTripByIdPort;
    private final UpdateTripPort updateTripPort;
    private final LoadTripsByPassengerPort loadTripsByPassengerPort;
    private final LoadTripsByDriverPort loadTripsByDriverPort;
    private final AppendTripEventPort appendTripEventPort;



    @Transactional
    public TripCreateResult createTrip(TripCreateCommand command) {

        // 도메인 정적 팩토리로 Trip 생성
        Trip trip = Trip.createTrip(
                command.policyId(),
                command.passengerId(),
                command.driverId(),
                command.dispatchId(),
                command.pickupAddress(),
                command.destinationAddress()
        );

        Trip saved = createTripPort.createTrip(trip);

        return new TripCreateResult(
                saved.getTripId(),
                saved.getStatus().name(),
                "운행 생성 완료 및 READY 상태 설정"
        );
    }

    public TripListResult loadTrips() {
        List<Trip> trips = loadTripsPort.findAll();

        List<TripItem> items = trips.stream()
                .map(TripItem::from)
                .toList();

        return new TripListResult(items);
    }

    public TripItem getTripDetail(UUID tripId) {
        Trip trip = loadTripByIdPort.loadTripById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        return TripItem.from(trip);
    }


    @Transactional
    public TripStartResult startTrip(StartTripCommand command) {
        UUID tripId = command.tripId();
        UUID notifierId = command.notifierId(); // 컨트롤러에서 헤더로 받은 값

        Trip trip = loadTripByIdPort.loadTripById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found. tripId=" + tripId));

        // 1) 상태 전이 + startTime 세팅 (Trip 도메인 메서드 내부에서 처리)
        boolean transitioned = trip.start(); // ✅ 상태 전이 여부

        if (transitioned) {
            appendTripEventPort.appendTripStarted(
                    trip.getTripId(),
                    command.notifierId(),
                    trip.getDispatchId(),
                    trip.getDriverId(),
                    trip.getPassengerId(),
                    trip.getPickupAddress(),
                    trip.getDestinationAddress(),
                    trip.getStartTime()
            );
        }

        return new TripStartResult(
                trip.getTripId(),
                trip.getStatus().name(),
                trip.getStartTime(),
                "운행이 시작되었습니다."
        );

    }
    @Transactional
    public TripEndResult endTrip(UUID tripId, EndTripCommand command) {
        Trip trip = loadTripByIdPort.loadTripById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found. tripId=" + tripId));

        // 1) 상태 전이 (STARTED -> ENDED)
        boolean transitioned = trip.end(command.totalDistance(), command.totalDuration());

        // 2) 업데이트 반영 (네 구조에선 포트로 update 하는 방식 유지)
        Trip updated = updateTripPort.updateTrip(trip);

        // 3) outbox 적재는 “진짜로 ENDED로 바뀐 경우만”
        if (transitioned) {
            appendTripEventPort.appendTripEnded(
                    updated.getTripId(),
                    command.notifierId(),
                    updated.getDispatchId(),
                    updated.getDriverId(),
                    updated.getPassengerId(),
                    updated.getPickupAddress(),
                    updated.getDestinationAddress(),
                    updated.getStartTime(),
                    updated.getEndTime(),
                    updated.getTotalDistance(),
                    updated.getTotalDuration(),
                    updated.getFinalFare()
            );
        }

        return new TripEndResult(
                updated.getTripId(),
                updated.getStatus().name(),
                updated.getEndTime(),
                updated.getFinalFare(),
                transitioned ? "운행이 종료되었습니다." : "이미 종료된 운행입니다."
        );
    }

    @Transactional
    public PassengerTripHistoryResult getPassengerTripHistory(UUID passengerId, int page, int size) {

        LoadTripsByPassengerPort.PassengerTripsPage tripsPage =
                loadTripsByPassengerPort.loadTripsByPassengerId(passengerId, page, size);

        List<TripHistoryItem> items = tripsPage.trips().stream()
                .map(TripHistoryItem::from)  // 이미 Trip -> TripItem 변환 메서드 있다고 가정
                .toList();

        return new PassengerTripHistoryResult(
                passengerId,
                tripsPage.totalCount(),
                page,
                size,
                items
        );
    }

    public DriverTripHistoryResult getDriverTripHistory(UUID driverId, int page, int size) {

        Page<Trip> tripPage = loadTripsByDriverPort.loadTripsByDriverId(driverId, page, size);

        var items = tripPage.getContent().stream()
                .map(TripHistoryItem::from)  // Trip → TripHistoryItem 변환
                .toList();

        return new DriverTripHistoryResult(
                driverId,
                tripPage.getTotalElements(),
                page,
                size,
                items
        );
    }



}
