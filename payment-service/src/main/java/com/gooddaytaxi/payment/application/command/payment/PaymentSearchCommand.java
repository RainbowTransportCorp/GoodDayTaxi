package com.gooddaytaxi.payment.application.command.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentSearchCommand(Integer page,
                                   Integer size,
                                   String method,
                                   String status,
                                   UUID passengerId,
                                   UUID driverId,
                                   UUID tripId,
                                   String searchPeriod,
                                   LocalDateTime startDay,
                                   LocalDateTime endDay,
                                   String sortBy,
                                   Boolean sortAscending) {
}
