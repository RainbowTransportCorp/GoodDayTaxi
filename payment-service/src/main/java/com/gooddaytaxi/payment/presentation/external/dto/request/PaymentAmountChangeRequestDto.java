package com.gooddaytaxi.payment.presentation.external.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentAmountChangeRequestDto(@NotNull UUID paymentId,
                                            @NotNull Long amount) {
}
