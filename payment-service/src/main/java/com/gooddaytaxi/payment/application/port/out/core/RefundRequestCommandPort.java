package com.gooddaytaxi.payment.application.port.out.core;

import com.gooddaytaxi.payment.domain.entity.RefundRequest;

public interface RefundRequestCommandPort {
    void save(RefundRequest request);
}
