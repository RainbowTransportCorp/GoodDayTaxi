package com.gooddaytaxi.payment.infrastructure.client.dto;

public record TossPayConfirmResponseDto(String orderId,
                                        String paymentKey,
                                        String requestedAt,
                                        String approvedAt,
                                        String method,   //결제 수단
                                        EasyPay easyPay,  //간편 결제 수단
                                        int totalAmount) {
    public record EasyPay(
            String provider //간편 결제 제공자
    ) {}
}
