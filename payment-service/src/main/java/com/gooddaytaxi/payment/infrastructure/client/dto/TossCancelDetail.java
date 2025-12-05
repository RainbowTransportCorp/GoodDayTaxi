package com.gooddaytaxi.payment.infrastructure.client.dto;

public record TossCancelDetail(
        Long cancelAmount,
        String cancelReason,
        String canceledAt,
        String transactionKey  //취소 건의 키값
) {}
