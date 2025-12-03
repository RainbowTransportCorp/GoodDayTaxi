package com.gooddaytaxi.payment.presentation.external.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentSearchRequestDto (Integer page,
                                       Integer size,
                                       String method,
                                       String status,
                                       UUID passengerId,
                                       UUID driverId,
                                       UUID tripId,
                                       @NotBlank String searchPeriod,
                                       String startDay,
                                       String endDay,
                                       String sortBy,
                                       @JsonProperty("sortAscending") Boolean sortAscending
                                       ){
}
