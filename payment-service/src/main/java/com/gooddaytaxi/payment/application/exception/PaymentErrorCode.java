package com.gooddaytaxi.payment.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {

    //payment
    //400 — BAD_REQUEST
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "P003", "유효하지 않은 금액입니다"),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "P004", "유효하지 않은 결제 상태입니다"),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "P004", "유효하지 않은 결제 수단입니다"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "P004", "유효하지 않은 유저 롤입니다"),
    INVALID_SORT_BY(HttpStatus.BAD_REQUEST, "P004", "유효하지 않은 정렬 조건입니다"),
    INVALID_SEARCH_PERIOD(HttpStatus.BAD_REQUEST, "P004", "유효하지 않은 검색 기간입니다"),
    PERIOD_REQUIRED_FOR_SEARCH(HttpStatus.BAD_REQUEST, "P004", "검색기간에 직접 입력 선택시 시작일과 종료일은 필수 값입니다"),
    PAYMENT_METHOD_NOT_TOSSPAY(HttpStatus.BAD_REQUEST, "P005", "결제 수단이 Toss Pay가 아닙니다"),
    PAYMENT_AMOUNT_SAME(HttpStatus.BAD_REQUEST, "P005", "변경하려는 결제 금액이 기존 금액과 동일합니다"),

    //403 — FORBIDDEN
    DRIVER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "P006", "해당 요청은 기사만 사용할 수 있습니다"),
    PASSENGER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "P006", "해당 요청은 승객만 사용할 수 있습니다"),
    PASSENGER_ROLE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "P006", "승객은 해당 요청을 수행할 수 없습니다"),
    PAYMENT_PASSENGER_MISMATCH(HttpStatus.FORBIDDEN, "P006", "결제 정보의 승객정보가 일치하지 않습니다"),
    PAYMENT_DRIVER_MISMATCH(HttpStatus.FORBIDDEN, "P007", "결제 정보의 기사정보가 일치하지 않습니다"),

    //404 — NOT_FOUND
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "해당하는 결제 정보를 찾을 수 없습니다"),
    TRIP_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "입력하신 운행아이디로 결제 정보를 찾을 수 없습니다"),

    //409 — CONFLICT
    PAYMENT_STATUS_INVALID(HttpStatus.CONFLICT, "P007", "현재 결제 상태에서는 요청한 작업을 수행할 수 없습니다"),

    //502 — BAD_GATEWAY
    TOSSPAY_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "P007", "토스페이 결제 승인 요청이 실패했습니다"),

    //공통요청
    PAYMENT_ERROR(HttpStatus.BAD_REQUEST, "P999", "Payment error");


    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
