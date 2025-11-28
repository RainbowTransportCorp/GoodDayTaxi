package com.gooddaytaxi.payment.presentation.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentCreateResponseDto {
    private UUID paymentId;
    private String method;
    private Long amount;
    private Long passengerId;
    private Long driverId;
    private UUID tripId;

}
