package com.gooddaytaxi.trip.application.service;


import com.gooddaytaxi.trip.application.command.StartTripCommand;
import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.port.out.CreateTripPort;
import com.gooddaytaxi.trip.application.port.out.LoadTripByIdPort;
import com.gooddaytaxi.trip.application.port.out.LoadTripsPort;
import com.gooddaytaxi.trip.application.port.out.UpdateTripPort;
import com.gooddaytaxi.trip.application.result.TripCreateResult;
import com.gooddaytaxi.trip.application.result.TripItem;
import com.gooddaytaxi.trip.application.result.TripListResult;
import com.gooddaytaxi.trip.application.result.TripStartResult;
import com.gooddaytaxi.trip.domain.model.Trip;
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



}
