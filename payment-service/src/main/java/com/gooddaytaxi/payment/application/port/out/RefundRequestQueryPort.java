package com.gooddaytaxi.payment.application.port.out;

import com.gooddaytaxi.payment.domain.entity.RefundRequest;

import java.util.Optional;
import java.util.UUID;

public interface RefundRequestQueryPort {


    Optional<RefundRequest> findById(UUID requestId);
}
