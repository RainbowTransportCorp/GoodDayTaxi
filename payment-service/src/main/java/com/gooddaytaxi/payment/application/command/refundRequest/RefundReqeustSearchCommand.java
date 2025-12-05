package com.gooddaytaxi.payment.application.command.refundRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefundReqeustSearchCommand(Integer page,
                                         Integer size,
                                         UUID paymentId,
                                         String status,  //환불 요청 상태
                                         String reasonKeyword,  //환불 사유 키워드
                                         String method,  //결제 수단
                                         UUID passengerId,
                                         UUID driverId,
                                         String searchPeriod,
                                         LocalDateTime startDay,
                                         LocalDateTime endDay,
                                         String sortBy,
                                         Boolean sortAscending) {
}
