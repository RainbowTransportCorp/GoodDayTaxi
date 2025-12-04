package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.gooddaytaxi.payment.application.port.out.RefundRequestQueryPort;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundRequestQueryAdapter implements RefundRequestQueryPort {
    private final RefundRequestRepository requestRepository;

    @Override
    public RefundRequest save(RefundRequest request) {
        return requestRepository.save(request);
    }
}
