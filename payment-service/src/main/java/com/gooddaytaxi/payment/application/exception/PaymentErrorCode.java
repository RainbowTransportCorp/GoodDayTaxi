package com.gooddaytaxi.payment.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {

    //payment
    //400 — BAD_REQUEST
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "PAY001", "유효하지 않은 금액입니다"),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "PAY002", "유효하지 않은 결제 상태입니다"),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "PAY003", "유효하지 않은 결제 수단입니다"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "PAY004", "유효하지 않은 유저 롤입니다"),
    INVALID_SORT_BY(HttpStatus.BAD_REQUEST, "PAY005", "유효하지 않은 정렬 조건입니다"),
    INVALID_SEARCH_PERIOD(HttpStatus.BAD_REQUEST, "PAY006", "유효하지 않은 검색 기간입니다"),
    INVALID_REFUND_STATUS(HttpStatus.BAD_REQUEST, "PAY007", "유효하지 않은 환불 상태입니다"),
    INVALID_REFUND_REASON(HttpStatus.BAD_REQUEST, "PAY008", "유효하지 않은 환불 이유입니다"),
    INVALID_PAYMENT_EVENT_TYPE(HttpStatus.BAD_REQUEST, "PAY009", "유효하지 않은 결제 이벤트 타입 입니다"),
    PERIOD_REQUIRED_FOR_SEARCH(HttpStatus.BAD_REQUEST, "PAY010", "검색기간에 직접 입력 선택시 시작일과 종료일은 필수 값입니다"),
    PAYMENT_METHOD_NOT_TOSSPAY(HttpStatus.BAD_REQUEST, "PAY011", "결제 수단이 Toss Pay가 아닙니다"),
    PAYMENT_AMOUNT_SAME(HttpStatus.BAD_REQUEST, "PAY012", "변경하려는 결제 금액이 기존 금액과 동일합니다"),
    PAYMENT_METHOD_SAME(HttpStatus.BAD_REQUEST, "PAY013", "변경하려는 결제 수단이 기존 수단과 동일합니다"),

    //403 — FORBIDDEN
    DRIVER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY014", "해당 요청은 기사만 사용할 수 있습니다"),
    PASSENGER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY015", "해당 요청은 승객만 사용할 수 있습니다"),
    ADMIN_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY016", "해당 요청은 관리자만 사용할 수 있습니다"),
    MASTER_ADMIN_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY017", "해당 요청은 최고관리자만 사용할 수 있습니다"),
    DRIVER_MASTER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY018", "해당 요청은 기사와 최고관리자만 사용할 수 있습니다"),
    PASSENGER_AND_DRIVER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY019", "해당 요청은 승객과 기사만 사용할 수 있습니다"),
    MASTER_AND_ADMIN_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "PAY020", "해당 요청은 관리자와 최고관리자만 사용할 수 있습니다"),
    PASSENGER_ROLE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "PAY021", "승객은 해당 요청을 수행할 수 없습니다"),
    PAYMENT_PASSENGER_MISMATCH(HttpStatus.FORBIDDEN, "PAY022", "결제 정보의 승객정보가 일치하지 않습니다"),
    PAYMENT_DRIVER_MISMATCH(HttpStatus.FORBIDDEN, "PAY023", "결제 정보의 기사정보가 일치하지 않습니다"),

    //404 — NOT_FOUND
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY024", "해당하는 결제 정보를 찾을 수 없습니다"),
    PAYMENT_ATTEMPT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY024", "해당하는 결제 시도 이력을 찾을 수 없습니다"),
    TRIP_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY025", "입력하신 운행아이디로 결제 정보를 찾을 수 없습니다"),

    //409 — CONFLICT
    PAYMENT_STATUS_INVALID(HttpStatus.CONFLICT, "PAY026", "현재 결제 상태에서는 요청한 작업을 수행할 수 없습니다"),
    DUPLICATE_PAYMENT_EXISTS(HttpStatus.CONFLICT, "PAY027", "이미 진행중인 결제 청구서가 있습니다"),
    COMPLETED_PAYMENT(HttpStatus.CONFLICT, "PAY028", "이미 완료된 결제 청구서가 있습니다"),
    IDEMPOTENCY_PAYMENT_CONFLICT(HttpStatus.CONFLICT, "PAY029", "이미 처리 중인 동일한 결제 요청이 있습니다"),
    IDEMPOTENCY_REFUND_CONFLICT(HttpStatus.CONFLICT, "PAY030", "이미 처리 중인 동일한 환불 요청이 있습니다"),
    PAYMENT_LOCK_TIMEOUT(HttpStatus.CONFLICT, "PAY031", "동시 결제 승인 요청이 처리 중입니다. 잠시 후 다시 시도해주세요."),

    //500-INTERNAL_SERVER_ERROR - 서버 내부의 시스템 장애
    PAYMENT_JPA_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY032", "결제 처리 중 내부 저장소 오류가 발생했습니다"),


    //502 — BAD_GATEWAY
    TOSSPAY_CONFIRM_FAILED(HttpStatus.BAD_GATEWAY, "PAY033", "토스페이 결제 승인 요청이 실패했습니다"),
    TOSSPAY_CANCEL_FAILED(HttpStatus.BAD_GATEWAY, "PAY034", "토스페이 결제 취소 요청이 실패했습니다"),

    //REFUNDREQUEST
    //404 — NOT_FOUND
    REFUND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY035", "해당하는 환불 요청 정보를 찾을 수 없습니다"),

    //403 — FORBIDDEN
    REFUND_REQUEST_NOT_PASSENGER(HttpStatus.FORBIDDEN, "PAY036", "해당 환불 요청의 작성자가 아닙니다"),
    REFUND_REQUEST_NOT_DRIVER(HttpStatus.FORBIDDEN, "PAY037", "해당 환불 요청의 담당 기사가 아닙니다"),

    //409 — CONFLICT
    REFUND_REQUEST_STATUS_INVALID(HttpStatus.CONFLICT, "PAY038", "현재 환불 요청의 상태에서는 요청한 작업을 수행할 수 없습니다"),

    //Refund
    //404 — NOT_FOUND
    REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY039", "해당하는 환불을 찾을 수 없습니다"),
    //409 — CONFLICT
    REFUND_REQUEST_PAYMENT_MISMATCH(HttpStatus.CONFLICT, "PAY040", "해당 환불 요청의 결제와 실제 결제가 맞지 않습니다."),


    //공통요청
    PAYMENT_ERROR(HttpStatus.BAD_REQUEST, "PAY999", "Payment error");


    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
