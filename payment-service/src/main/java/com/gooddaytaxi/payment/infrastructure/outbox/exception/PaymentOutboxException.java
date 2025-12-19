package com.gooddaytaxi.payment.infrastructure.outbox.exception;

public class PaymentOutboxException extends RuntimeException {
    public PaymentOutboxException(String message) {
        super(message);
    }

    //근본 윈인이 되는 예외를 포함할 수 있도록 생성자 추가
    public PaymentOutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
