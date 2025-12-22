package com.gooddaytaxi.trip.application.service;


import com.gooddaytaxi.trip.application.command.*;
import com.gooddaytaxi.trip.application.port.out.*;
import com.gooddaytaxi.trip.application.result.*;
import com.gooddaytaxi.trip.domain.model.Trip;
import com.gooddaytaxi.trip.domain.model.enums.TripStatus;
import com.gooddaytaxi.trip.domain.model.enums.UserRole;
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
    private final TripLocationStatePort tripLocationStatePort;
    private final LoadActiveTripByDriverPort loadActiveTripByDriverPort;
    private final LoadActiveTripByPassengerPort loadActiveTripByPassengerPort;


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
    public TripCancelResult cancelTrip(CancelTripCommand command) {
        UUID tripId = command.tripId();

        Trip trip = loadTripByIdPort.loadTripById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found. tripId=" + tripId));

        // 1) 상태 전이 (CREATED/READY만 가능)
        boolean transitioned = trip.cancel();

        // 2) DB 업데이트 반영 (상태가 바뀌었든 아니든 저장은 일단 통일)
        Trip updated = updateTripPort.updateTrip(trip);

        // 3) outbox 적재는 “진짜로 CANCELLED로 바뀐 경우만”
        if (transitioned) {
            appendTripEventPort.appendTripCanceled(
                    updated.getTripId(),
                    command.notifierId(),
                    updated.getDispatchId(),
                    updated.getDriverId(),
                    updated.getPassengerId(),
                    command.cancelReason().name(),
                    LocalDateTime.now()
            );
        }

        return new TripCancelResult(
                updated.getTripId(),
                updated.getStatus().name(),
                transitioned ? "운행이 취소되었습니다." : "취소할 수 없는 상태입니다."
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

    @Transactional
    public TripLocationUpdatedResult publishLocationUpdate(UpdateTripLocationCommand command) {

        Trip trip = loadTripByIdPort.loadTripById(command.tripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found. tripId=" + command.tripId()));

        // STARTED에서만 발행
        if (trip.getStatus() != TripStatus.STARTED) {
            throw new IllegalStateException(
                    "STARTED 상태에서만 위치 업데이트 이벤트를 발행할 수 있습니다. status=" + trip.getStatus()
            );
        }

        // region 변경 감지 + sequence 계산 (DB lock)
        TripLocationStatePort.NextSequenceResult locationChange =
                tripLocationStatePort.computeNext(trip.getTripId(), command.region());

        // 변경 없으면 발행 안 함 (스킵)
        if (!locationChange.changed()) {
            return new TripLocationUpdatedResult(
                    trip.getTripId(),
                    false,
                    locationChange.nextSequence(),
                    locationChange.previousRegion(),
                    locationChange.currentRegion()
            );
        }

        // 변경 있으면 outbox 적재
        appendTripEventPort.appendTripLocationUpdated(
                trip.getTripId(),
                command.notifierId(),
                trip.getDispatchId(),
                trip.getDriverId(),
                command.currentAddress(),
                command.region(),
                locationChange.previousRegion(),
                locationChange.nextSequence(),
                java.time.LocalDateTime.now()
        );

        return new TripLocationUpdatedResult(
                trip.getTripId(),
                true,
                locationChange.nextSequence(),
                locationChange.previousRegion(),
                locationChange.currentRegion()
        );
    }


    @Transactional
    public TripItem getActiveTripByPassenger(UUID passengerId, UserRole role) {

        Trip trip = loadActiveTripByPassengerPort
            .loadActiveTripByPassengerId(passengerId)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Active trip not found for passengerId=" + passengerId
                )
            );

        return TripItem.from(trip);
    }

    @Transactional
    public TripItem getActiveTripByDriver(UUID driverId, UserRole role) {

        Trip trip = loadActiveTripByDriverPort
            .loadActiveTripByDriverId(driverId)
            .orElseThrow(() ->
                new EntityNotFoundException(
                    "Active trip not found for driverId=" + driverId
                )
            );

        return TripItem.from(trip);
    }

}
