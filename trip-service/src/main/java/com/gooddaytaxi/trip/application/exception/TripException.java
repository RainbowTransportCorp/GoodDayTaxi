package com.gooddaytaxi.trip.application.exception;

import org.springframework.http.HttpStatus;

public class TripException extends RuntimeException {

    private final TripErrorCode errorCode;

    public TripException(TripErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TripException(TripErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public TripErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }

    public HttpStatus getStatus() {
        return errorCode.getStatus();
    }
}
