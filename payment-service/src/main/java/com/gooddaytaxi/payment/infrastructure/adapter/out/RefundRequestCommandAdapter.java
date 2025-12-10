package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.gooddaytaxi.payment.application.port.out.core.RefundRequestCommandPort;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundRequestCommandAdapter implements RefundRequestCommandPort {
    private final RefundRequestRepository requestRepository;

    @Override
    public void save(RefundRequest request) {
        requestRepository.save(request);
    }
}
