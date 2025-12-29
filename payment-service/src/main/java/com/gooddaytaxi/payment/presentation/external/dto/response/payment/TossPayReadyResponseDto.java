package com.gooddaytaxi.payment.presentation.external.dto.response.payment;

import java.util.UUID;

public record TossPayReadyResponseDto(
    String orderId,
    String customerKey,
    Long amount
) {

    /**
     * TossPay 결제 준비 응답 생성
     *
     * @param tripId 운행 ID
     * @param userId 사용자 ID
     * @param amount 결제 금액
     */
    public static TossPayReadyResponseDto of(
        UUID tripId,
        UUID userId,
        Long amount
    ) {
        return new TossPayReadyResponseDto(
            "order-" + tripId,
            "customer-" + userId,
            amount
        );
    }
}
