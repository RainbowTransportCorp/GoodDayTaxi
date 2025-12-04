package com.gooddaytaxi.payment.application.result.payment;

import java.time.LocalDateTime;

public record AttemptReadResult (String status,
                                 String pgMethod,
                                 String pgProvider,
                                 LocalDateTime approvedAt,
                                 String failDetail
                                 ){
}
