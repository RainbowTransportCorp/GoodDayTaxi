package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.gooddaytaxi.payment.application.port.out.RefundRequestCommandPort;
import com.gooddaytaxi.payment.domain.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundRequestCommandAdapter implements RefundRequestCommandPort {
    private final RefundRequestRepository requestRepository;
}
