package com.gooddaytaxi.payment.application.result;

import java.util.UUID;

public record PaymentTossPayResult (UUID paymentId,
                                    Long amount,
                                    String status,
                                    String method) {
}
