package com.gooddaytaxi.payment.application.message;

import lombok.Getter;

@Getter
public enum SuccessMessage {
    PAYMENT_CREATE_SUCCESS("결제 청구서 발행이 완료되었습니다"),
    PAYMENT_APPROVE_SUCCESS("결제 승인에 성공하였습니다"),
    PAYMENT_UPDATE_AMOUNT_SUCCESS("결제 금액 변경에 성공하였습니다"),
    PAYMENT_UPDATE_METHOD_SUCCESS("결제 수단 변경에 성공하였습니다"),
    PAYMENT_CANCEL_SUCCESS("결제 취소에 성공하였습니다");


    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }
}
