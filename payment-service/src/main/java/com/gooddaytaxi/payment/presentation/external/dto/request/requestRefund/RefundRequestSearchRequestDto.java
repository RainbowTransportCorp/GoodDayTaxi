package com.gooddaytaxi.payment.presentation.external.dto.request.requestRefund;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefundRequestSearchRequestDto(Integer page,
                                            Integer size,
                                            String status,  //환불 요청 상태
                                            String reasonKeyword,  //환불 사유 키워드
                                            @NotBlank String searchPeriod,
                                            String startDay,
                                            String endDay,
                                            String sortBy,
                                            @JsonProperty("sortAscending") Boolean sortAscending) {
}
