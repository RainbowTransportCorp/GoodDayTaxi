package com.gooddaytaxi.trip.presentation.mapper.result;

import com.gooddaytaxi.trip.application.result.PassengerTripHistoryResult;
import com.gooddaytaxi.trip.application.result.TripHistoryItem;
import com.gooddaytaxi.trip.presentation.dto.response.PassengerTripHistoryResponse;
import com.gooddaytaxi.trip.presentation.dto.response.TripHistoryItemResponse;
import org.springframework.stereotype.Component;


import java.util.List;
@Component
public class PassengerTripHistoryResponseMapper {

    public PassengerTripHistoryResponse toResponse(PassengerTripHistoryResult result) {

        List<TripHistoryItemResponse> tripResponses = result.trips().stream()
                .map(this::toTripHistoryItemResponse)
                .toList();

        return new PassengerTripHistoryResponse(
                result.passengerId(),   // UUID
                result.totalCount(),    // 전체 건수
                result.page(),          // 현재 페이지
                result.size(),          // 페이지 크기
                tripResponses           // 변환된 리스트
        );
    }

    private TripHistoryItemResponse toTripHistoryItemResponse(TripHistoryItem item) {
        return new TripHistoryItemResponse(
                item.tripId().toString(),
                item.status(),
                item.pickupAddress(),
                item.destinationAddress(),
                item.startTime(),
                item.endTime(),
                item.totalDistance(),
                item.totalDuration(),
                item.finalFare()
        );
    }
}
