package com.gooddaytaxi.payment.infrastructure.adapter.out;

import com.gooddaytaxi.payment.application.port.out.RefundRequestQueryPort;
import com.gooddaytaxi.payment.domain.entity.RefundRequest;
import com.gooddaytaxi.payment.domain.repository.RefundRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefundRequestQueryAdapter implements RefundRequestQueryPort {
    private final RefundRequestRepository requestRepository;



    @Override
    public Optional<RefundRequest> findById(UUID requestId) {
        return requestRepository.findById(requestId);
    }

    @Override
    public Page<RefundRequest> searchRefundRequests(UUID paymentId, String status, String reasonKeyword, String method, UUID passeangerId, UUID driverId, LocalDateTime startDay, LocalDateTime endDay, Pageable pageable) {
        return requestRepository.searchRefundRequests(paymentId, status, reasonKeyword, method, passeangerId, driverId, startDay, endDay, pageable);
    }
}
