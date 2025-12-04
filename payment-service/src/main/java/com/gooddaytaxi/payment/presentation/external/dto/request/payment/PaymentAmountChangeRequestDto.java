package com.gooddaytaxi.payment.presentation.external.dto.request.payment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentAmountChangeRequestDto(@NotNull UUID paymentId,
                                            @NotNull Long amount) {
}
