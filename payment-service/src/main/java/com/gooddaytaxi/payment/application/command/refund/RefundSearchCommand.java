package com.gooddaytaxi.payment.application.command.refund;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundSearchCommand(Integer page,
                                  Integer size,
                                  String status,
                                  String reason,
                                  Boolean existRequest,
                                  UUID passengerId,
                                  UUID driverId,
                                  UUID tripId,
                                  String method,
                                  Long minAmount,
                                  Long maxAmount,
                                  String searchPeriod,
                                  LocalDateTime startDay,
                                  LocalDateTime endDay,
                                  String sortBy,
                                  Boolean sortAscending) {
}
