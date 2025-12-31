package com.gooddaytaxi.payment.presentation.external.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PaymentSearchRequestDto (Integer page,
                                       Integer size,
                                       String method,
                                       String status,
                                       @NotBlank String searchPeriod,
                                       String startDay,
                                       String endDay,
                                       String sortBy,
                                       @JsonProperty("sortAscending") Boolean sortAscending
                                       ){
}
