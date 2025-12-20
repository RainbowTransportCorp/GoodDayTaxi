package com.gooddaytaxi.payment.presentation.external.dto.request.refund;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record RefundSearchRequestDto(Integer page,
                                     Integer size,
                                     String status,  //환불상태
                                     String reason,  //환불사유(enum값)
                                     @JsonProperty("existRequest") Boolean existRequest, //환불요청 존재 여부
                                     UUID tripId,
                                     String method,
                                     Long minAmount,   //최소 환불금액
                                     Long maxAmount,   //최대 환불금액
                                     @NotBlank String searchPeriod,  //canceledAt 기준
                                     String startDay,
                                     String endDay,
                                     String sortBy,
                                     @JsonProperty("sortAscending") Boolean sortAscending) {
}
