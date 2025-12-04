package com.gooddaytaxi.payment.presentation.external.dto.request.payment;

public record PaymentTossPayRequestDto (String paymentKey,
                                        String orderId,
                                        Long amount){
}
