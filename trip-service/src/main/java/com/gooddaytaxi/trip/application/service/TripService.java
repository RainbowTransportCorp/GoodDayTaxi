package com.gooddaytaxi.trip.application.service;


import com.gooddaytaxi.trip.application.command.TripCreateCommand;
import com.gooddaytaxi.trip.application.port.out.CreateTripPort;
import com.gooddaytaxi.trip.application.port.out.LoadTripByIdPort;
import com.gooddaytaxi.trip.application.port.out.LoadTripsPort;
import com.gooddaytaxi.trip.application.result.TripCreateResult;
import com.gooddaytaxi.trip.application.result.TripItem;
import com.gooddaytaxi.trip.application.result.TripListResult;
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



}
