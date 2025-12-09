package com.gooddaytaxi.trip.application.service;


import com.gooddaytaxi.trip.application.command.EndTripCommand;
import com.gooddaytaxi.trip.application.command.StartTripCommand;
import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.port.out.*;
import com.gooddaytaxi.trip.application.result.*;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    @Transactional
    public TripCreateResult createTrip(TripCreateCommand command) {

        // 도메인 정적 팩토리로 Trip 생성
        Trip trip = Trip.createTrip(
                command.policyId(),
                command.passengerId(),
                command.driverId(),
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

        Trip trip = loadTripByIdPort.loadTripById(command.tripId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trip not found: " + command.tripId()
                )); // → GlobalExceptionHandler에서 404로 맵핑되게 해 두면 됨

        // 도메인 로직 호출 (READY → STARTED)
        trip.start();

        Trip saved = updateTripPort.updateTrip(trip);

        return new TripStartResult(
                saved.getTripId(),
                saved.getStatus().name(),
                saved.getStartTime(),
                "운행이 시작되었습니다."
        );
    }

    public TripEndResult endTrip(UUID tripId, EndTripCommand command) {
        Trip trip = loadTripByIdPort.loadTripById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));

        if (trip.getStatus() == TripStatus.ENDED) {
            throw new IllegalStateException("이미 ENDED 상태인 운행입니다.");
        }

        trip.end(command.totalDistance(), command.totalDuration());

        Trip updated = updateTripPort.updateTrip(trip);

        return new TripEndResult(
                updated.getTripId(),
                updated.getStatus().name(),
                updated.getEndTime(),
                updated.getFinalFare(),
                "운행 종료, 요금 산정 완료 및 결제 요청 이벤트 발행 완료"
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



}
