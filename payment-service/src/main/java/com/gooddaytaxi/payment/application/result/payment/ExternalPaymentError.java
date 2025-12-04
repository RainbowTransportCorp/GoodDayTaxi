package com.gooddaytaxi.payment.application.result.payment;

public record ExternalPaymentError(
        int status,
        String providerMessage,   // Toss에서 온 에러 메시지(파싱 결과)
        String rawBody            // 원본 응답(로그용)
) { }
