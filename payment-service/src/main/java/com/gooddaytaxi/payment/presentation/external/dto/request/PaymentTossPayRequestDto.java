package com.gooddaytaxi.payment.presentation.external.dto.request;

public record PaymentTossPayRequestDto (String paymentKey,
                                        String orderId,
                                        Long amount){
}
